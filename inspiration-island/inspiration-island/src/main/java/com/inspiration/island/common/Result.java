package com.inspiration.island.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 成功
    public static <T> Result<T> success(T data){
        return new Result<>(200,"操作成功",data);
    }
    public static <T> Result<T> success(){
        return new Result<>(200,"操作成功",null);
    }

    // 失败
    public static <T> Result<T> error(Integer code,String msg){
        return new Result<>(code,msg,null);
    }
}