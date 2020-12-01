package user;

import utils.Pair;

public interface GeneralUserManagement {

    Pair<LoginResponse, String> login(String userName, String password);

    int tokenToID(String token);

    boolean isAdmin(int id);

    TokenResponse checkToken(String token);

    String getFreeUserName(String name);
}
