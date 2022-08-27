package com.landingis.api.controller;

import com.landingis.api.cfg.tenants.master.MasterDatabaseConfig;
import com.landingis.api.constant.LandingISConstant;
import com.landingis.api.dto.ApiMessageDto;
import com.landingis.api.dto.ErrorCode;
import com.landingis.api.dto.ResponseListObj;
import com.landingis.api.dto.device.DeviceDto;
import com.landingis.api.dto.device.TokenDto;
import com.landingis.api.exception.RequestException;
import com.landingis.api.form.device.*;
import com.landingis.api.intercepter.MyAuthentication;
import com.landingis.api.jwt.JWTUtils;
import com.landingis.api.jwt.UserJwt;
import com.landingis.api.mapper.DeviceMapper;
import com.landingis.api.storage.master.criteria.DeviceCriteria;
import com.landingis.api.storage.master.model.Account;
import com.landingis.api.storage.master.model.Customer;
import com.landingis.api.storage.master.model.DbConfig;
import com.landingis.api.storage.master.model.Device;
import com.landingis.api.storage.master.repository.AccountRepository;
import com.landingis.api.storage.master.repository.CustomerRepository;
import com.landingis.api.storage.master.repository.DbConfigRepository;
import com.landingis.api.storage.master.repository.DeviceRepository;
import com.landingis.api.utils.AESUtils;
import com.landingis.api.utils.DateUtils;
import com.landingis.api.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/v1/device")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class DeviceController extends ABasicController {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    MasterDatabaseConfig masterDatabaseConfig;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    AccountRepository accountRepository;


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListObj<DeviceDto>> list(DeviceCriteria deviceCriteria, Pageable pageable) {
        if (!isAdmin()) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED, "Not allowed get list.");
        }
        ApiMessageDto<ResponseListObj<DeviceDto>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Device> list;
        if(deviceCriteria.getParentId() != null){
            list = deviceRepository.findAll(deviceCriteria.getSpecification(), Pageable.unpaged());
        } else {
            list = deviceRepository.findAll(deviceCriteria.getSpecification(), pageable);
        }

        ResponseListObj<DeviceDto> responseListObj = new ResponseListObj<>();
        responseListObj.setData(deviceMapper.fromEntityListToDtoList(list.getContent()));
        responseListObj.setPage(pageable.getPageNumber());
        responseListObj.setTotalPage(list.getTotalPages());
        responseListObj.setTotalElements(list.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<DeviceDto> get(@PathVariable("id") Long id) {
        if (!isAdmin()) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED, "Not allowed get.");
        }
        ApiMessageDto<DeviceDto> result = new ApiMessageDto<>();
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        result.setData(deviceMapper.fromEntityToDto(device));
        result.setMessage("Get device success");
        return result;
    }

    private String generateToken(Long customerId,Long deviceId, String posId, String tenantId, String appendStringRole, String deviceType, Long parentId, String parentPosId, Long expireTime) {
        LocalDateTime parsedDate = LocalDateTime.now();


        UserJwt qrJwt = new UserJwt();
        qrJwt.setAccountId(customerId);

        qrJwt.setDeviceId(posId);
        qrJwt.setPosId(deviceId);
        qrJwt.setPemission(appendStringRole);

        qrJwt.setTenantId(tenantId);
        qrJwt.setKind(deviceType);

        if (deviceType.equals(LandingISConstant.DEVICE_TYPE_REMVIEW.toString())) {
            qrJwt.setParentId(parentId);
            qrJwt.setParentToken(parentPosId);
        }
        parsedDate = parsedDate.plusMinutes(expireTime);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new MyAuthentication(qrJwt));
        return JWTUtils.createJWT(JWTUtils.ALGORITHMS_RSA, "authenticationToken.getId().toString()", qrJwt, DateUtils.convertToDateViaInstant(parsedDate));
    }

    @PostMapping(value = "/verify-device", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<TokenDto> verifyDevice(@Valid @RequestBody VerifyDeviceForm verifyDeviceForm, BindingResult bindingResult) {
        ApiMessageDto<TokenDto> result = new ApiMessageDto<>();
        Device device = deviceRepository.findByPosId(verifyDeviceForm.getPosId());
        if (device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        if (!device.getSessionId().equals(verifyDeviceForm.getSessionId())){
            throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST, "Wrong session");
        }
        checkActive(device.getCustomer());
        if(device == null){
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND);
        }
        if(device.getExtDate() == null){
            device.setExtDate(device.getExpireDate());
        }

        DbConfig dbConfig = dbConfigRepository.findByDeviceId(device.getId());
        if (dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND);
        } else if (!dbConfig.isInitialize()) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_INITIALIZE);
        }

        String appendStringRole = LandingISConstant.REMVIEW_PERMISISONS;
        if(device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)){
            appendStringRole = LandingISConstant.POS_PERMISSIONS;
        }
        long expireTime = 24 * 60;    // 1 day by minute
        String token = generateToken(device.getCustomer().getId(),device.getId(),device.getPosId(),dbConfig.getName(),appendStringRole,device.getType().toString(),null, null,expireTime);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);
        tokenDto.setEnabledRemview(device.getEnabledRemview());
        tokenDto.setDeviceId(device.getPosId());
        tokenDto.setNewSessionId(null);

        device.setTimeLastUsed(new Date());

        result.setData(tokenDto);
        result.setMessage("Verify success");
        return result;
    }

    @GetMapping(value = "/request-qrcode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> requestQrCode(@RequestParam("accountId") Long accountId, @RequestParam("deviceId") Long deviceId) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if(device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE)){
            throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST);
        }
        if (!device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED);
        }
        Customer customer = customerRepository.findById(accountId).orElse(null);
        if(customer == null || !customer.getStatus().equals(LandingISConstant.STATUS_ACTIVE)){
            throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST);
        }
        String qrcode = AESUtils.encrypt((new Date()).getTime() + LandingISConstant.DELIM + LandingISConstant.PASSWORD + LandingISConstant.DELIM + getSessionFromToken().getTenantId() + LandingISConstant.DELIM + getSessionFromToken().getPosId() + LandingISConstant.DELIM + getSessionFromToken().getAccountId() + LandingISConstant.DELIM + StringUtils.generateRandomString(10), true);
        ApiMessageDto<String> result = new ApiMessageDto<>();
        result.setData(qrcode);
        result.setMessage("Request qrcode success");
        return result;
    }

    private String[] parsedQrCode(String qrcode) {
        String decryptQrCode = AESUtils.decrypt(qrcode, true);
        return decryptQrCode.split(LandingISConstant.DELIM);
    }

    @PostMapping(value = "/verify-qrcode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<TokenDto> verifyQrCode(@Valid @RequestBody VerifyQRCodeForm verifyQRCodeForm, BindingResult bindingResult) {

        String[] dataFromQrCode = parsedQrCode(verifyQRCodeForm.getQrCode());
        Long generatedTime = Long.parseLong(dataFromQrCode[0]);
        String password = dataFromQrCode[1];
        String tenantId = dataFromQrCode[2];
        Long deviceId = Long.valueOf(dataFromQrCode[3]);
        Long customerId = Long.parseLong(dataFromQrCode[4]);
        Device devicePos = deviceRepository.findById(deviceId).orElse(null);
        checkActive(devicePos);
        DbConfig dbConfig = dbConfigRepository.findByName(tenantId);
        if (dbConfig == null) {
            throw new RequestException(ErrorCode.DB_CONFIG_ERROR_NOT_FOUND);
        }
        if (new Date().getTime() - generatedTime >= LandingISConstant.MAX_TIME_VERIFY_QRCODE) {
            if (devicePos.getIsDemo() == null || !devicePos.getIsDemo()){
                throw new RequestException(ErrorCode.DEVICE_ERROR_QRCODE_EXPIRED);
            }
        }
        if (!password.equals(LandingISConstant.PASSWORD)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_QRCODE_PASSWORD_NOT_MATCHED);
        }
        Customer customer = customerRepository.findById(customerId).orElse(null);
        checkActive(customer);
        Device device = deviceRepository.findByPosIdAndParentId(verifyQRCodeForm.getDeviceId(), devicePos.getId());
        if (device == null) {
            device = new Device();
            device.setName("device_" + verifyQRCodeForm.getDeviceId());
            device.setCustomer(customer);
            if(devicePos.getIsDemo() != null && devicePos.getIsDemo()){
                device.setStatus(LandingISConstant.STATUS_ACTIVE);
            }else{
                device.setStatus(LandingISConstant.STATUS_PENDING);
            }
            device.setPlatform(verifyQRCodeForm.getPlatform());
            device.setParent(devicePos);
            device.setPosId(verifyQRCodeForm.getDeviceId());
            device.setType(LandingISConstant.DEVICE_TYPE_REMVIEW);
            deviceRepository.save(device);
        } else {
            device.setTimeLastUsed(new Date());
            deviceRepository.save(device);
        }
        ApiMessageDto<TokenDto>  result = new ApiMessageDto<>();

        String appendStringRole = LandingISConstant.VERIFY_QRCODE_PERMISSION;

        long expireTime = 10 * 365 * 24 * 60;   // 10 years by minute
        String token = generateToken(customerId,device.getId(), verifyQRCodeForm.getDeviceId(), tenantId, appendStringRole, LandingISConstant.DEVICE_TYPE_REMVIEW.toString(), devicePos.getId(), devicePos.getPosId(), expireTime);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);
        tokenDto.setDeviceId(device.getPosId());
        tokenDto.setPosName(device.getParent().getName());
        tokenDto.setPosId(device.getParent().getId());
        tokenDto.setPosToken(device.getParent().getPosId());
        tokenDto.setTenantId(tenantId);
        tokenDto.setIsActive(Objects.equals(device.getStatus(), LandingISConstant.STATUS_ACTIVE));
        result.setData(tokenDto);
        result.setMessage("Verify success");
        return result;
    }

    @PostMapping(value = "/verify-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<TokenDto> verifyToken() {
        ApiMessageDto<TokenDto> result = new ApiMessageDto<>();
        UserJwt currentSession = getSessionFromToken();

        Device parent = new Device();
        if(currentSession.getParentId() != null){
            parent = deviceRepository.findById(currentSession.getParentId()).orElse(null);
            if (parent == null || !parent.getStatus().equals(LandingISConstant.STATUS_ACTIVE) || !parent.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
            }
            checkActive(parent.getCustomer());
        }
        // generate access token
        long timeout = 1;     // 1 hours by minute
        Device device = deviceRepository.findById(currentSession.getPosId()).orElse(null);
        if (device == null || !device.getStatus().equals(LandingISConstant.STATUS_ACTIVE)) {
            result.setResult(false);
            result.setMessage("Device not found or not active");
            return result;
        }
        checkActive(device.getCustomer());

        // generate an access token with expire time 2 hours
        String appendStringRole = LandingISConstant.REMVIEW_PERMISISONS;
        if(device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)){
            appendStringRole = LandingISConstant.POS_PERMISSIONS;
        }
        String accessToken = generateToken(currentSession.getAccountId(),device.getId(), device.getPosId(), currentSession.getTenantId(), appendStringRole, LandingISConstant.DEVICE_TYPE_REMVIEW.toString(), currentSession.getParentId(), parent.getPosId(), timeout);

        if(device.getExtDate() == null){
            device.setExtDate(device.getExpireDate());
        }

        String tokenPermissions = currentSession.getPemission();
        String parentToken = currentSession.getParentToken();
        if (device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            TokenDto tokenDto = new TokenDto();
            if (tokenPermissions.equals(LandingISConstant.VERIFY_QRCODE_PERMISSION)) {
                result.setMessage("No need verify");
            } else {
                long expireTime = 24 * 60;     // 1 day by minute
                String token = generateToken(currentSession.getAccountId(),device.getId(),device.getPosId(),currentSession.getTenantId(),appendStringRole,LandingISConstant.DEVICE_TYPE_POS.toString(),null,null, expireTime);
                tokenDto.setToken(token);
            }
            tokenDto.setIsAdmin(device.getIsAdmin());
            tokenDto.setAccessToken(accessToken);
            tokenDto.setPermission(device.getPermission());
            tokenDto.setLatLongLimit(device.getLatLongLimit());
            tokenDto.setDistanceLimit(device.getDistanceLimit());
            tokenDto.setEnabledRemview(device.getEnabledRemview());
            result.setData(tokenDto);
        } else {
            boolean isGenerateToken = false;
            if (!tokenPermissions.equals(LandingISConstant.VERIFY_QRCODE_PERMISSION) || !parentToken.equals(parent.getPosId())) {
                isGenerateToken = true;
            }
            TokenDto tokenDto = new TokenDto();
            tokenDto.setAccessToken(accessToken);
            tokenDto.setPermission(device.getPermission());
            if (!isGenerateToken) {
                result.setMessage("No need verify");
                tokenDto.setIsAdmin(device.getIsAdmin());
                tokenDto.setLatLongLimit(device.getLatLongLimit());
                tokenDto.setDistanceLimit(device.getDistanceLimit());
                result.setData(tokenDto);
                return result;
            }
            long expireTime = 10 * 365 * 24 * 60;     // 10 years by minute
            String token = generateToken(currentSession.getAccountId(),device.getId(),device.getPosId(),currentSession.getTenantId(),appendStringRole,LandingISConstant.DEVICE_TYPE_POS.toString(),parent.getId(), parent.getPosId(), expireTime);

            tokenDto.setIsAdmin(device.getIsAdmin());
            tokenDto.setLatLongLimit(device.getLatLongLimit());
            tokenDto.setDistanceLimit(device.getDistanceLimit());
            tokenDto.setToken(token);
            result.setData(tokenDto);
        }
        return result;
    }

    private void validateDeviceForm(Integer deviceType, String sessionId, Boolean enabledRemview) {
        if (deviceType.equals(LandingISConstant.DEVICE_TYPE_POS)) {
            if (sessionId == null) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST, "sessionId is required");
            }
            if (enabledRemview == null) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST, "enabledRemview is required");
            }
        }
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody CreateDeviceForm createDeviceForm, BindingResult bindingResult) {
        if (!isAdmin()) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED, "Not allowed to create.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        validateDeviceForm(createDeviceForm.getType(), createDeviceForm.getSessionId(), createDeviceForm.getEnabledRemview());
        if (!createDeviceForm.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST, "Only allow create POS device");
        }
        Customer customer = customerRepository.findById(createDeviceForm.getCustomerId()).orElse(null);
        if (customer == null || !customer.getStatus().equals(LandingISConstant.STATUS_ACTIVE)) {
            throw new RequestException(ErrorCode.CUSTOMER_ERROR_NOT_FOUND, "Customer not found");
        }

        Device device = deviceMapper.fromCreateFormToEntity(createDeviceForm);
        if(!isSuperAdmin()){
            device.setExpireDate(null);
            device.setExtDate(null);
        }
        device.setSessionId(createDeviceForm.getSessionId());
        device.setEnabledRemview(createDeviceForm.getEnabledRemview());
        if (deviceRepository.findByPosId(device.getPosId()) != null) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_DEVICE_EXISTS, "Pos id exists");
        }
        deviceRepository.save(device);
        apiMessageDto.setMessage("Create device success");
        return apiMessageDto;
    }


    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<DeviceDto> update(@Valid @RequestBody UpdateDeviceForm updateDeviceForm, BindingResult bindingResult) {
        if (!isAdmin()) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED, "Not allowed to update.");
        }
        ApiMessageDto<DeviceDto> apiMessageDto = new ApiMessageDto<>();

        Device device = deviceRepository.findById(updateDeviceForm.getId()).orElse(null);
        if (device == null) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND, "Not found device.");
        }
        validateDeviceForm(device.getType(), updateDeviceForm.getSessionId(), updateDeviceForm.getEnabledRemview());

        if (device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            device.setSessionId(updateDeviceForm.getSessionId());
            device.setEnabledRemview(updateDeviceForm.getEnabledRemview());
        }
        // old date
        LocalDate expired = device.getExpireDate();
        LocalDate extDate = device.getExtDate();

        deviceMapper.fromUpdateFormToEntity(updateDeviceForm, device);
        if(!isSuperAdmin()){
            device.setExpireDate(expired);
            device.setExtDate(extDate);
        }
        deviceRepository.save(device);
        apiMessageDto.setData(deviceMapper.fromEntityToDto(device));
        apiMessageDto.setMessage("Update device success");
        return apiMessageDto;
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private void checkDeviceDate(Device device) {
        Date currentDate = new Date();
        LocalDate extDateCheck = device.getExtDate() != null ? device.getExtDate() : device.getExpireDate();
        if (device.getExpireDate() != null && extDateCheck != null) {
            Date expireDate = convertToDateViaInstant(device.getExpireDate());
            Date extDate = convertToDateViaInstant(extDateCheck);
            if (currentDate.after(extDate) && currentDate.after(expireDate)) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_UNACTIVE);
            }
        }
    }

    @PutMapping(value = "/mobile-device-update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> updateMobileDevice(@Valid @RequestBody UpdateMobileDeviceForm updateMobileDeviceForm, BindingResult bindingResult) {
        ApiMessageDto<String> result = new ApiMessageDto<>();
        UserJwt currentSession = getSessionFromToken();
        if (!currentSession.getKind().equals(LandingISConstant.DEVICE_TYPE_REMVIEW.toString())) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED);
        }
        Device device = deviceRepository.findById(currentSession.getPosId()).orElse(null);
        checkActive(device);
        checkDeviceDate(device);
        Customer customer = customerRepository.findById(currentSession.getAccountId()).orElse(null);
        checkActive(customer);
        device.setName(updateMobileDeviceForm.getDeviceName());
        deviceRepository.save(device);
        result.setMessage("Update success");
        return result;
    }

    @GetMapping(value = "/mobile-device-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListObj<DeviceDto>> getDeviceMobileList(DeviceCriteria deviceCriteria, Pageable pageable) {
        UserJwt currentSession = getSessionFromToken();
        if (!currentSession.getKind().equals(LandingISConstant.DEVICE_TYPE_POS.toString())) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED);
        }
        Device devicePos = deviceRepository.findById(currentSession.getPosId()).orElse(null);
        checkActive(devicePos);

        ApiMessageDto<ResponseListObj<DeviceDto>> responseListObjApiMessageDto = new ApiMessageDto<>();

        deviceCriteria.setParentId(devicePos.getId());
        Page<Device> list = deviceRepository.findAll(deviceCriteria.getSpecification(), pageable);
        ResponseListObj<DeviceDto> responseListObj = new ResponseListObj<>();
        responseListObj.setData(deviceMapper.fromEntityListToDtoListAutoComplete(list.getContent()));
        responseListObj.setPage(pageable.getPageNumber());
        responseListObj.setTotalPage(list.getTotalPages());
        responseListObj.setTotalElements(list.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list success");
        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/mobile-device-delete")
    public ApiMessageDto<String> mobileDeviceDelete(@RequestParam(name = "deviceId") Long deviceId) {
        UserJwt currentSession = getSessionFromToken();
        Device device = deviceRepository.findById(currentSession.getPosId()).orElse(null);
        if (device == null) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND);
        }
        if (device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            if (deviceId == null) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_BAD_REQUEST, "deviceId is required");
            }
            Device deletingDevice = deviceRepository.findById(deviceId).orElse(null);
            if (deletingDevice == null) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND);
            }
            if (!deletingDevice.getParent().getId().equals(device.getId())) {
                throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED);
            }
            deviceRepository.delete(deletingDevice);
        } else {
            deviceRepository.delete(device);
        }

        ApiMessageDto<String> result = new ApiMessageDto<>();
        result.setMessage("Delete success");
        return result;
    }

    private String parseDatabaseNameFromConnectionString(String url) {
        String cleanString = url.substring("jdbc:mysql://".length(), url.indexOf("?"));
        return cleanString.substring(cleanString.indexOf("/") + 1);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApiMessageDto<DeviceDto> delete(@PathVariable("id") Long id) {
        Account admin = accountRepository.findById(getCurrentUserId()).orElse(null);
        if (admin == null || !admin.getKind().equals(LandingISConstant.USER_KIND_ADMIN)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_UNAUTHORIZED);
        }
        checkActive(admin);
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null || !device.getType().equals(LandingISConstant.DEVICE_TYPE_POS)) {
            throw new RequestException(ErrorCode.DEVICE_ERROR_NOT_FOUND);
        }
        DbConfig dbConfig = dbConfigRepository.findByDeviceId(device.getId());
        if (dbConfig != null) {
            try (
                    Connection connection = masterDatabaseConfig.masterDataSource().getConnection();
                    Statement statement = connection.createStatement();
            ) {
                String databaseName = parseDatabaseNameFromConnectionString(dbConfig.getUrl());
                statement.execute("DROP USER '" + dbConfig.getUsername() + "'@'%';");
                statement.execute("DROP USER '" + dbConfig.getUsername() + "'@'localhost';");
                statement.execute("DROP DATABASE " + databaseName + ";");
                dbConfigRepository.delete(dbConfig);
            } catch (SQLException e) {
                e.printStackTrace();
//                    throw new RequestException(ErrorCode.DEVICE_ERROR_SQL_DELETE_ERROR);
            }
        }
        ApiMessageDto<DeviceDto> result = new ApiMessageDto<>();
        deviceRepository.delete(device);
        result.setData(deviceMapper.fromEntityToDto(device));
        result.setMessage("Delete device success");
        return result;
    }
}
