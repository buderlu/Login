package UserAutenticationService.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import UserAutenticationService.dto.requestDto.LoginRequestDto;
import UserAutenticationService.dto.requestDto.SignupRequestDto;
import UserAutenticationService.dto.responseDto.JwtResponseDto;
import UserAutenticationService.dto.responseDto.MessageResponseDto;
import UserAutenticationService.persistence.repository.RoleRepository;
import UserAutenticationService.persistence.repository.UserRepository;
import UserAutenticationService.security.jwt.JwtUtils;
import UserAutenticationService.security.service.UserDetailsImpl;
import UserAutenticationService.serviceCommunication.IdPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import UserAutenticationService.persistence.Entity.ERoleEntity;
import UserAutenticationService.persistence.Entity.RoleEntity;
import UserAutenticationService.persistence.Entity.UserEntity;




@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;


	@Autowired
	IdPublisher idPublisher;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@PostMapping("signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponseDto(jwt,
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	//@PreAuthorize("hasRole('User')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto signUpRequestDto) {
		if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponseDto("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponseDto("Error: Email is already in use!"));
		}

		// Create new user's account
		UserEntity user = new UserEntity(signUpRequestDto.getUsername(),
							 signUpRequestDto.getEmail(),
							 encoder.encode(signUpRequestDto.getPassword()));

		Set<String> strRoles = signUpRequestDto.getRoles();
		Set<RoleEntity> roleEntities = new HashSet<>();

		if (strRoles == null) {
			RoleEntity userRoleEntity = roleRepository.findByName(ERoleEntity.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roleEntities.add(userRoleEntity);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
			/*	case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;

			 */
				default:
					RoleEntity userRoleEntity = roleRepository.findByName(ERoleEntity.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roleEntities.add(userRoleEntity);
				}
			});
		}
		//user.setId();
		System.out.println(user.getId());
		user.setRoles(roleEntities);
		user.setId(UUID.randomUUID().toString());


		new IdPublisher().publishUserIdOnExchange(user.getId(),rabbitTemplate);
		//rabbitExchangePublisher.publishUserIdOnExchange(user.getId(),rabbitTemplate);
		System.out.println(user.getId());
		userRepository.save(user);


		return ResponseEntity.ok(new MessageResponseDto("User registered successfully!"));
	}
}
