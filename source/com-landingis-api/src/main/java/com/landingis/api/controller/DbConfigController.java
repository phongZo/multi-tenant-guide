package com.landingis.api.controller;


import com.landingis.api.cfg.tenants.ScriptRunner;
import com.landingis.api.cfg.tenants.TenantDbContext;
import com.landingis.api.cfg.tenants.master.MasterDatabaseConfig;
import com.landingis.api.cfg.tenants.tenant.DataSourceBasedMultiTenantConnectionProviderImpl;
import com.landingis.api.constant.LandingISConstant;
import com.landingis.api.dto.ApiMessageDto;
import com.landingis.api.dto.ErrorCode;
import com.landingis.api.dto.UploadFileDto;
import com.landingis.api.dto.dbConfig.DbConfigDto;
import com.landingis.api.exception.RequestException;
import com.landingis.api.form.UploadFileForm;
import com.landingis.api.form.dbConfig.CreateDbConfigForm;
import com.landingis.api.form.dbConfig.UpdateDbConfigForm;
import com.landingis.api.jwt.UserJwt;
import com.landingis.api.mapper.DbConfigMapper;
import com.landingis.api.service.LandingIsApiService;
import com.landingis.api.storage.master.model.DbConfig;
import com.landingis.api.storage.master.model.Device;
import com.landingis.api.storage.master.repository.DbConfigRepository;
import com.landingis.api.storage.master.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
@RequestMapping("/v1/db-config")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class DbConfigController extends ABasicTenantController{
    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DbConfigMapper dbConfigMapper;

    @Autowired
    MasterDatabaseConfig masterDatabaseConfig;

    @Autowired
    @Qualifier("datasourceBasedMultitenantConnectionProvider")
    DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProvider;

    @Autowired
    LandingIsApiService landingIsApiService;

    @GetMapping(value = "/get/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<DbConfigDto> get(@PathVariable("deviceId") Long deviceId) {
        if(!isAdmin()){
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED, "Not allowed get.");
        }
        ApiMessageDto<DbConfigDto> result = new ApiMessageDto<>();

        DbConfig dbConfig = dbConfigRepository.findByDeviceId(deviceId);
        if(dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND, "Not found db config.");
        }
        result.setData(dbConfigMapper.fromEntityToDto(dbConfig));
        result.setMessage("Get db config success");
        return result;
    }

    private String parseDatabaseNameFromConnectionString(String url) {
        String cleanString = url.substring("jdbc:mysql://".length(), url.indexOf("?"));
        return cleanString.substring(cleanString.indexOf("/") + 1);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody CreateDbConfigForm createDbConfigForm, BindingResult bindingResult) {
        if(!isAdmin()){
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED, "Not allowed to create.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Device device = deviceRepository.findById(createDbConfigForm.getDeviceId()).orElse(null);
        if (device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE) || !device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        checkActive(device.getCustomer());

        DbConfig dbConfig = dbConfigMapper.fromCreateFormToEntity(createDbConfigForm);
        dbConfig.setName("tenant_id_" + device.getId()); //tenant_id_[id nha hang]

        String databaseName = parseDatabaseNameFromConnectionString(dbConfig.getUrl());
        // Open a connection
        try(
            Connection connection = masterDatabaseConfig.masterDataSource().getConnection();
            Statement statement = connection.createStatement();
        ) {
            statement.execute("CREATE DATABASE " + databaseName  + " CHARACTER SET utf8;");
            statement.execute("CREATE USER '" + dbConfig.getUsername() + "' IDENTIFIED BY '" + dbConfig.getPassword() + "';");
            statement.execute("CREATE USER '" + dbConfig.getUsername() + "'@'localhost' IDENTIFIED BY '" + dbConfig.getPassword() + "';");
            statement.execute("GRANT ALL PRIVILEGES ON " + databaseName +".* TO '" + dbConfig.getUsername() + "';");
            statement.execute("FLUSH PRIVILEGES;");

            System.out.println("Tenant database created successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_CANNOT_CREATE_DB);
        }

        dbConfigRepository.save(dbConfig);

        apiMessageDto.setMessage("Create db config success");
        return apiMessageDto;

    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateDbConfigForm updateDbConfigForm, BindingResult bindingResult) {
        if(!isAdmin()){
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED, "Not allowed to update.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        DbConfig dbConfig = dbConfigRepository.findById(updateDbConfigForm.getId()).orElse(null);
        if(dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND, "Not found db config.");
        }
        dbConfigMapper.fromUpdateFormToEntity(updateDbConfigForm, dbConfig);
        dbConfigRepository.save(dbConfig);
        apiMessageDto.setMessage("Update db config success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        if(!isAdmin()){
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED, "Not allowed to delete.");
        }
        ApiMessageDto<String> result = new ApiMessageDto<>();

        DbConfig dbConfig = dbConfigRepository.findById(id).orElse(null);
        if(dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND, "Not found db config");
        }
        dbConfigRepository.delete(dbConfig);
        result.setMessage("Delete db config success");
        return result;
    }

    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> restore(@Valid UploadFileForm uploadFileForm, BindingResult bindingResult) {
        /**
         * Ref:
         *  https://github.com/BenoitDuffez/ScriptRunner
         *  https://gist.github.com/joe776/831762
         * */
        ApiMessageDto<String> result = new ApiMessageDto<>();

        UserJwt currentSession = getSessionFromToken();
        if(!currentSession.getKind().equals(LandingISConstant.DEVICE_TYPE_POS.toString())) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED);
        }
        Device device = deviceRepository.findById(getCurrentDeviceId()).orElse(null);
        if (device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        checkActive(device.getCustomer());
        if(!Boolean.TRUE.equals(device.getEnabledRemview())) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_DEVICE_NOT_ENABLED_SYNC);
        }
        DbConfig dbConfig = dbConfigRepository.findByDeviceId(device.getId());
        if(dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND);
        } else if(!Boolean.TRUE.equals(dbConfig.isInitialize())) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_INITIALIZE);
        }
        ApiMessageDto<UploadFileDto> uploadFileDtoApiMessageDto = landingIsApiService.storeFile(uploadFileForm);
        String finalFile = uploadFileDtoApiMessageDto.getData().getFilePath().replace(File.separator + "DOCUMENT" + File.separator, "");
        Resource resource = landingIsApiService.loadFileAsResource("DOCUMENT", finalFile);

        try(
                Connection connection = dataSourceBasedMultiTenantConnectionProvider.getConnection(TenantDbContext.getCurrentTenant());
                InputStream inputStream = resource.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(resource.getFile()));
            if(md5.equalsIgnoreCase(uploadFileForm.getMd5())){
                ScriptRunner runner = new ScriptRunner(connection, true, true);
                runner.runScript(reader);
                System.out.println("Restore database successfully... "+ TenantDbContext.getCurrentTenant() + ", md5: "+md5);
                result.setMessage("Restore success");
            }else{
                throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UPLOAD);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(!(e instanceof  RequestException)){
                throw new RequestException(ErrorCode.DB_CONFIG_ERROR_CANNOT_CREATE_DB);
            }
            result.setResult(false);
            result.setMessage("Restore error");
        }

        // Delete backup file
        landingIsApiService.deleteFile(uploadFileDtoApiMessageDto.getData().getFilePath());


        return result;
    }

    public ApiMessageDto<String> restore_cmd(@Valid UploadFileForm uploadFileForm, BindingResult bindingResult) {
        UserJwt currentSession = getSessionFromToken();
        if(!currentSession.getKind().equals(LandingISConstant.DEVICE_TYPE_POS.toString())) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_UNAUTHORIZED);
        }
        Device device = deviceRepository.findById(getCurrentDeviceId()).orElse(null);
        if (device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        checkActive(device.getCustomer());
        if(!Boolean.TRUE.equals(device.getEnabledRemview())) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_DEVICE_NOT_ENABLED_SYNC);
        }
        DbConfig dbConfig = dbConfigRepository.findByDeviceId(device.getId());
        if(dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND);
        } else if(!Boolean.TRUE.equals(dbConfig.isInitialize())) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_INITIALIZE);
        }
        ApiMessageDto<UploadFileDto> uploadFileDtoApiMessageDto = landingIsApiService.storeFile(uploadFileForm);

        int processComplete = -1;
        String finalFile = uploadFileDtoApiMessageDto.getData().getFilePath().replace(File.separator + "DOCUMENT" + File.separator, "");
        Resource resource = landingIsApiService.loadFileAsResource("DOCUMENT", finalFile);
        try {
            String[] command = new String[]{
                    LandingISConstant.MYSQL_ENVIRONMENT_PATH,
                    "-u" + dbConfig.getUsername(),
                    "-p" + dbConfig.getPassword(),
                    "-e",
                    " source " + resource.getFile().getAbsolutePath(),
                    parseDatabaseNameFromConnectionString(dbConfig.getUrl())
            };
            Process runtimeProcess = Runtime.getRuntime().exec(command);
            processComplete = runtimeProcess.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_CANNOT_RESTORE_DB);
        }
        if(processComplete != 0) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_CANNOT_RESTORE_DB);
        }

        // Delete backup file
        landingIsApiService.deleteFile(uploadFileDtoApiMessageDto.getData().getFilePath());

        ApiMessageDto<String> result = new ApiMessageDto<>();
        result.setMessage("Restore success");
        return result;
    }
}
