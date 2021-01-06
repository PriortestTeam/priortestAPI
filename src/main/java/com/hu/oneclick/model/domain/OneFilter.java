package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;

import java.io.Serializable;

/**
 * @author qingyang
 */
public class OneFilter implements VerifyParam,Serializable {


    private String type;



    @Override
    public void verify() throws BizException {

    }

}
