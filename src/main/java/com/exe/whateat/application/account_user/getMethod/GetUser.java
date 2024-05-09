package com.exe.whateat.application.account_user.getMethod;

import com.exe.whateat.application.account_user.getMethod.dto.UserDTO;
import com.exe.whateat.application.account_user.getMethod.mapper.AccountDTOMapper;
import com.exe.whateat.application.account_user.getMethod.response.UserResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

public final class GetUser {

    @RestController
    @AllArgsConstructor
    public static class GetUserContronller extends AbstractController {

        private final GetUserService getUserService;

        @GetMapping("/users")
        public ResponseEntity<UserResponse> getAllAccount(
                @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
        ) {
            final UserResponse response = getUserService.getAllUser(pageNumber, pageSize);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;

        public UserResponse getAllUser(int pageNumber, int pageSize) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Account> accountPage = accountRepository.findAll(pageable);
            List<Account> accounts = accountPage.getContent();
            List<UserDTO> userDTOS = accounts
                    .stream()
                    .map(u -> accountDTOMapper.apply(u)).collect(Collectors.toList());
            UserResponse userResponse =
                    UserResponse.builder()
                            .userDTOS(userDTOS)
                            .pageNumber(pageNumber)
                            .pageSize(pageSize)
                            .totalElemnts(userDTOS.size())
                            .build();
            return userResponse;
        }
    }
}
