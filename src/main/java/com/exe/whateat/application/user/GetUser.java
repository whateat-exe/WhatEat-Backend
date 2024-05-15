package com.exe.whateat.application.user;

import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetUser {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for get users"
    )
    public static class GetUserController extends AbstractController {

        private final GetUserService getUserService;

        @GetMapping("/users")
        public ResponseEntity<List<UserResponse>> getAllAccount(
                @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
        ) {
            final List<UserResponse> response = getUserService.getAllUser(pageNumber, pageSize);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;

        public List<UserResponse> getAllUser(int pageNumber, int pageSize) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Account> accountPage = accountRepository.findAll(pageable);
            List<Account> accounts = accountPage.getContent();
            return  accounts
                    .stream()
                    .map(accountDTOMapper::convertToDto).toList();
        }
    }
}
