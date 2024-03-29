package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import communication.packets.request.LoginRequestPacket;
import user.LoginResponse;

/**
 * A login response sent as result of an login request using an {@link LoginRequestPacket}.
 */
public class LoginResponsePacket extends ResponsePacket {

    @Expose
    private String token;

    /**
     * @param token      A token used for further communication on a successful login, null otherwise.
     */
    public LoginResponsePacket(String token) {
        this(RequestResult.Valid, token);
    }

    /**
     * @param result     A String representing the overall result of the login request. This {@link RequestResult} is an abstraction of a {@link LoginResponse}.
     * @param token      A token used for further communication on a successful login, null otherwise.
     */
    private LoginResponsePacket(RequestResult result, String token) {
        super(PacketType.LOGIN_RESPONSE, result);
        this.token = token;
    }
}
