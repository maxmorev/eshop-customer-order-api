package ru.maxmorev.eshop.customer.order.api.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCart;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;
import ru.maxmorev.eshop.customer.order.api.request.RequestShoppingCartSet;
import ru.maxmorev.eshop.customer.order.api.services.ShoppingCartService;


import javax.validation.Valid;
import java.util.Locale;

@Slf4j
@RestController
@AllArgsConstructor
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;
    private MessageSource messageSource;


    @RequestMapping(path = "/shoppingCart/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ShoppingCart getShoppingCart(@PathVariable(name = "id", required = true) Long id, Locale locale) throws Exception {
        return shoppingCartService
                .findShoppingCartById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                messageSource
                                        .getMessage("shoppingCart.error.id",
                                                new Object[]{id},
                                                locale)));
    }

    @RequestMapping(path = "/shoppingCart/", method = RequestMethod.POST)
    @ResponseBody
    public ShoppingCart addToShoppingCartSet(@RequestBody @Valid RequestShoppingCartSet requestShoppingCartSet, Locale locale) {
        log.info("POST:> RequestShoppingCartSet :> {}", requestShoppingCartSet);
        return shoppingCartService
                .addBranchToShoppingCart(new ShoppingCartId(
                                requestShoppingCartSet.getBranchId(),
                                requestShoppingCartSet.getShoppingCartId()),
                        requestShoppingCartSet.getCommodityInfo());
    }

    @RequestMapping(path = "/shoppingCart/", method = RequestMethod.DELETE)
    @ResponseBody
    public ShoppingCart removeFromShoppingCartSet(@RequestBody @Valid RequestShoppingCartSet requestShoppingCartSet, Locale locale) {
        return shoppingCartService.removeBranchFromShoppingCart(
                new ShoppingCartId(
                        requestShoppingCartSet.getBranchId(),
                        requestShoppingCartSet.getShoppingCartId()),
                requestShoppingCartSet.getCommodityInfo().getAmount());
    }


}
