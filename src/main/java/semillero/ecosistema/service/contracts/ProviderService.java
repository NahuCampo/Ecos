package semillero.ecosistema.service.contracts;

import semillero.ecosistema.Dto.ProviderRequestDto;
import semillero.ecosistema.Dto.ProviderResponseDto;
import semillero.ecosistema.entity.ProviderEntity;

import java.util.List;

public interface ProviderService {

    List<ProviderResponseDto> getAll();

    List<ProviderResponseDto> getByName(String name);

    ProviderEntity save(Long userId, ProviderRequestDto providerRequestDto);

//    ProviderEntity update(Long userId,ProviderRequestDto providerRequestDto);
}