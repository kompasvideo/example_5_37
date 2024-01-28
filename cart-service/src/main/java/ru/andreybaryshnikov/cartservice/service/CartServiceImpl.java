package ru.andreybaryshnikov.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.andreybaryshnikov.cartservice.model.Cart;
import ru.andreybaryshnikov.cartservice.model.dto.CartDto;
import ru.andreybaryshnikov.cartservice.repository.CartRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    @Override
    public void addToCart(String xRequestId, long xUserId, CartDto cartDto) {
        log.info("---addToCart 1 ---");
        log.info("---addToCart 1 --- xRequestId - " + xRequestId);
        log.info("---addToCart 1 --- xUserId - " + xUserId);
        log.info("---addToCart 1 --- cartDto - " + cartDto);
        Cart cart = modelMapper.map(cartDto, Cart.class);
        log.info("---addToCart 2 ---");
        cart.setXRequestId(xRequestId);
        cart.setUserId(xUserId);
        log.info("---addToCart 3 ---");
        log.info("---addToCart 3 --- cart - " + cart);
        Cart cartResult = cartRepository.save(cart);
        log.info("---addToCart 4 ---");
        log.info("---addToCart 3 --- cart - " + cartResult);
    }

    @Override
    public List<CartDto> getCart(String xRequestId, long xUserId) {
        log.info("--- getToCart 1 ---");
        List<Cart> carts = cartRepository.findByUserId(xUserId);
        log.info("--- getToCart 2 ---");
        Type listCart = new TypeToken<List<CartDto>>() {}.getType();
        log.info("--- getToCart 3 ---");
        List<CartDto> cartDtos = modelMapper.map(carts, listCart);
        log.info("--- getToCart 4 ---");
        return cartDtos;
    }

    @Override
    public void clearCart(String xRequestId, long xUserId) {
        log.info("--- clearCart 1 ---");
        List<Cart> carts = cartRepository.findByUserId(xUserId);
        log.info("--- clearCart 2 ---");
        List<Long> ids = new ArrayList<>();
        log.info("--- clearCart 3 ---");
        for(Cart cart : carts) ids.add(cart.getId());
        log.info("--- clearCart 4 ---");
        cartRepository.deleteAllByIdInBatch(ids);
        log.info("--- clearCart 5 ---");
    }
}
