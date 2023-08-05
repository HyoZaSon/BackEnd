package com.help.hyozason_backend.etc;


public abstract class ResponseService {
    private HelpResponse.ResponseMap result;
    public HelpResponse setResponse(int code, String message, Object object) throws Exception {
        result = new HelpResponse.ResponseMap();
        result.setCode(code);
        result.setResponseData(message, object);
        return result;
    }

    public HelpResponse addResponse(String message, Object object) throws Exception {
        if(result == null)
            result = new HelpResponse.ResponseMap();
        result.setResponseData(message, object);
        return result;
    }

    public HelpResponse getResult() {
        return result;
    }

    public void closeResult() {
        if(result != null)
            result.clear();
    }
}
