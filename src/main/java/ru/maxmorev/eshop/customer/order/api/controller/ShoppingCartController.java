package ru.maxmorev.eshop.customer.order.api.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCart;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;
import ru.maxmorev.eshop.customer.order.api.request.RemoveFromCartRequest;
import ru.maxmorev.eshop.customer.order.api.request.ShoppingCartSetRequest;
import ru.maxmorev.eshop.customer.order.api.response.Message;
import ru.maxmorev.eshop.customer.order.api.services.ShoppingCartService;

import javax.validation.Valid;
import java.util.Locale;

@Slf4j
@RestController
@AllArgsConstructor
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;
    private MessageSource messageSource;

    @RequestMapping(path = "/shoppingCart/new/", method = RequestMethod.POST)
    @ResponseBody
    public ShoppingCart createEmptySoppingCart() {
        return shoppingCartService.createEmptyShoppingCart();
    }

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
    public ShoppingCart addToShoppingCartSet(@RequestBody @Valid ShoppingCartSetRequest requestShoppingCartSet, Locale locale) {
        log.info("POST:> RequestShoppingCartSet :> {}", requestShoppingCartSet);
        return shoppingCartService
                .addBranchToShoppingCart(new ShoppingCartId(
                                requestShoppingCartSet.getBranchId(),
                                requestShoppingCartSet.getShoppingCartId()),
                        requestShoppingCartSet.getPurchaseInfo().toEntity());
    }

    @RequestMapping(path = "/shoppingCart/", method = RequestMethod.DELETE)
    @ResponseBody
    public ShoppingCart removeFromShoppingCartSet(@RequestBody @Valid RemoveFromCartRequest removeFromCartRequest, Locale locale) {
        return shoppingCartService.removeBranchFromShoppingCart(
                new ShoppingCartId(
                        removeFromCartRequest.getBranchId(),
                        removeFromCartRequest.getShoppingCartId()),
                        removeFromCartRequest.getAmount());
    }

    @RequestMapping(path = "/shoppingCart/{id}/clear", method = RequestMethod.PUT)
    @ResponseBody
    public ShoppingCart cleanShoppingCart(@PathVariable(name = "id") Long id, Locale locale) {
        return shoppingCartService.cleanShoppingCart(id);
    }

    @RequestMapping(path = "/shoppingCart/{id}/clear", method = RequestMethod.DELETE)
    @ResponseBody
    public Message removeOrphanShoppingCart(@PathVariable(name = "id") Long id, Locale locale) {
        shoppingCartService.removeOrphan(id);
        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }

    @RequestMapping(path = "/shoppingCart/merge/from/{fromId}/to/{toId}", method = RequestMethod.POST)
    @ResponseBody
    public ShoppingCart mergeCartFromTo(@PathVariable(name="fromId") Long from,
                                        @PathVariable(name="toId") Long to) {
        return shoppingCartService.mergeCartFromTo(from, to);

    }

}
