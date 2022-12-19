package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
//    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;


    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(userEntity);

        UserDto returnUserDto = modelMapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User Not Found");
                });

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        /* UsingRestTemplate **/
//        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
//
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });
//
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using feign client */
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrders(userId);
//        } catch (FeignException ex){
//            log.error(ex.getMessage());
//        }
        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        userDto.setOrders(orderList);
        return userDto;

    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("not found username");
        });
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException("not found username");
        });
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

        UserDto userDto = mapper.map(userEntity, UserDto.class);
        return userDto;

    }
}
