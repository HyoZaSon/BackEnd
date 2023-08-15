package com.help.hyozason_backend.etc;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
// Login response class
public class HelpResponse {
    private int code = HttpStatus.OK.value();
    private Object result;

    //private boolean success;
    //private String message;
    public HelpResponse() {
    }

    ;

    public void setResult(Object result) {
        this.result = result;
    }

    public static class ResponseMap extends HelpResponse {

        private Map responseData = new HashMap();

        public ResponseMap() {
            setResult(responseData);
        }

        public void setResponseData(String key, Object value) {
            this.responseData.put(key, value);
        }

        public void clear() {
            this.responseData.clear();
        }

    }
}
