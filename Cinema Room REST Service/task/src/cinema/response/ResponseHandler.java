package cinema.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(UUID token, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", token);
        map.put("ticket", responseObj);
        return new ResponseEntity<Object>(map,status);
    }
}
