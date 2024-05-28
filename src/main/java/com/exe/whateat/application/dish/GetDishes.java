//package com.exe.whateat.application.dish;
//
//import com.exe.whateat.application.common.request.PaginationRequest;
//import com.exe.whateat.application.dish.mapper.DishMapper;
//import com.exe.whateat.application.dish.response.DishesResponse;
//import com.exe.whateat.infrastructure.repository.DishRepository;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.coyote.Response;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RestController;
//
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//public class GetDishes {
//
//    @Data
//    private static final class GetDishesRequest extends PaginationRequest {
//
//    }
//
//    @RestController
//    @AllArgsConstructor
//    @Tag(
//            name = "dish",
//            description = "get dishes"
//    )
//    public static class GetDishesController {
//
//        private GetDishesService getDishesService;
//
//        public ResponseEntity<Object> getDishes(@Valid GetDishesRequest getDishesRequest) {
//
//        }
//    }
//
//    @Service
//    @AllArgsConstructor
//    public static class GetDishesService {
//
//        private DishRepository dishRepository;
//        private DishMapper dishMapper;
//
//        public DishesResponse getDishes(GetDishesRequest getDishesRequest) {
//
//        }
//    }
//}
