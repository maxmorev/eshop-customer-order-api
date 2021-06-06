package ru.maxmorev.eshop.customer.order.api.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RestResponse<T> {
    private String status;
    T data;
    private String errorMessage;

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<T>()
                .setData(data)
                .setStatus("success");
    }

    public static <T> RestResponse<T> fail(String errorMessage) {
        return new RestResponse<T>()
                .setErrorMessage(errorMessage)
                .setStatus("fail");
    }
}
