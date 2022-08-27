package com.landingis.api.controller;


import com.landingis.api.jwt.UserJwt;

public class ABasicTenantController extends ABasicController{
    protected String getCurrentPosId(){
        UserJwt jwt = getSessionFromToken();
        return jwt == null ? null : jwt.getDeviceId();
    }

    protected Long getCurrentDeviceId(){
        UserJwt jwt = getSessionFromToken();
        return jwt == null ? null : jwt.getPosId();
    }
}
