package semillero.ecosistema.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import semillero.ecosistema.entity.UserEntity;
import semillero.ecosistema.exception.EmailExistException;
import semillero.ecosistema.exception.ErrorResponse;
import semillero.ecosistema.exception.UserNotExistException;
import semillero.ecosistema.service.contracts.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Secured("ADMIN")
    @GetMapping("/all")
    public ResponseEntity<List<UserEntity>> getAll() {
        List<UserEntity> userEntityList = userService.getAll();
        try {
            if(userEntityList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(userEntityList);
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ADMIN")
    @GetMapping("/all-rol")
    public ResponseEntity<List<UserEntity>> getAllByRol(@RequestParam String rol) {
        List<UserEntity> userEntityList = userService.getAllByRol(rol);
        try {
            if(userEntityList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(userEntityList);
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ADMIN")
    @GetMapping("/get-email")
    public ResponseEntity<UserEntity> getByEmail(@RequestParam String email) {
        UserEntity userEntity = userService.getByEmail(email);
        try {
            if(userEntity == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(userEntity);
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ADMIN")
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UserEntity userEntity) {

        try {
            return new ResponseEntity(userService.save(userEntity), HttpStatus.CREATED);
        } catch (EmailExistException emailExistException) {
            String errorMessage = "El correo electronico ya existe";
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ADMIN")
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestParam String email, @RequestBody UserEntity userEntity) {
        try {
            return new ResponseEntity(userService.update(email, userEntity), HttpStatus.OK);
        } catch (UserNotExistException userNotExistException) {
            String errorMessage = "El usuario no existe";
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ADMIN")
    @PatchMapping("disable-id")
    public ResponseEntity<Boolean> disabledUserById(@RequestParam String userId) {
        return new ResponseEntity<Boolean>(userService.disabledUserById(userId) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Secured("ADMIN")
    @PatchMapping("disable-email")
    public ResponseEntity<Boolean> disabledUserByEmail(@RequestParam String email) {
        return new ResponseEntity<Boolean>(userService.disabledUserByEmail(email) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}