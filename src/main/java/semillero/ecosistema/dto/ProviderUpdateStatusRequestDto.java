package semillero.ecosistema.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProviderUpdateStatusRequestDto {
    private String providerId;
    private String newStatus;

//    @NotEmpty (message = "Campo obligatorio.")
//    @Size(min = 20, max = 300, message = "Debe contener entre 20 y 300 caracteres.")
    private String newFeedBack;
}
