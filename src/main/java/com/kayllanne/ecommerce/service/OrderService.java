package com.kayllanne.ecommerce.service;

import com.kayllanne.ecommerce.dto.OrderItemResponse;
import com.kayllanne.ecommerce.dto.OrderResponse;
import com.kayllanne.ecommerce.dto.PaymentRequest;
import com.kayllanne.ecommerce.dto.PaymentResponse;
import com.kayllanne.ecommerce.entity.Cart;
import com.kayllanne.ecommerce.entity.CartItem;
import com.kayllanne.ecommerce.entity.Order;
import com.kayllanne.ecommerce.entity.OrderItem;
import com.kayllanne.ecommerce.entity.OrderStatus;
import com.kayllanne.ecommerce.entity.Product;
import com.kayllanne.ecommerce.entity.User;
import com.kayllanne.ecommerce.exception.ResourceNotFoundException;
import com.kayllanne.ecommerce.repository.CartRepository;
import com.kayllanne.ecommerce.repository.OrderItemRepository;
import com.kayllanne.ecommerce.repository.OrderRepository;
import com.kayllanne.ecommerce.repository.ProductRepository;
import com.kayllanne.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrderFromCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + username));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Não é possível criar um pedido com um carrinho vazio.");
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto (ID: " + cartItem.getProduct().getId() + ") do item do carrinho não encontrado ou foi removido."));

            orderItems.add(OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build());
        }
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);


        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + orderId));
        return mapToOrderResponse(order);
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + orderId));
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + request.getOrderId()));
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            return PaymentResponse.builder()
                    .orderId(order.getId())
                    .amountPaid(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus("FAILED")
                    .newOrderStatus(order.getStatus())
                    .paymentDate(LocalDateTime.now())
                    .message("Valor do pagamento não corresponde ao total do pedido.")
                    .build();
        }
        String transactionId = UUID.randomUUID().toString();

        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
        } else {
            return PaymentResponse.builder()
                    .orderId(order.getId())
                    .amountPaid(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus("FAILED")
                    .newOrderStatus(order.getStatus())
                    .paymentDate(LocalDateTime.now())
                    .transactionId(transactionId)
                    .message("Pedido não está no status PENDING para ser pago. Status atual: " + order.getStatus())
                    .build();
        }
        return PaymentResponse.builder()
                .orderId(order.getId())
                .amountPaid(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .paymentStatus("SUCCESS")
                .newOrderStatus(order.getStatus())
                .paymentDate(LocalDateTime.now())
                .message("Pagamento processado com sucesso!")
                .build();
    }
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .username(order.getUser().getUsername())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .imageUrl(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}