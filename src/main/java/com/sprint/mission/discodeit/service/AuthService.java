package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authentication.LoginRequest;
import com.sprint.mission.discodeit.dto.authentication.LoginResponse;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
