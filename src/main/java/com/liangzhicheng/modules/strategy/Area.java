package com.liangzhicheng.modules.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    //地区类型：国家，省，市，区
    private String type;
    //地区id
    private String id;

}
