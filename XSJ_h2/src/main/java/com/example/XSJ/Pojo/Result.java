package com.example.XSJ.Pojo;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    // 添加构造函数
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    //返回数据的成功调用

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "操作成功", data);
    }

    public static Result success() {
        return new Result(0, "操作成功", null);
    }

    public static Result error() {
        return new Result(1, "操作失败", null);
    }

    public static <E> Result<E> error(E data) {
        return new Result<>(1, "操作失败", data);
    }
}