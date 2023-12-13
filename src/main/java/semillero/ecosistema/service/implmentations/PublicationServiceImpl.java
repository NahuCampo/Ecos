package semillero.ecosistema.service.implmentations;

import com.mysql.cj.log.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import semillero.ecosistema.Dto.PublicationRequestDto;
import semillero.ecosistema.Dto.PublicationResponseDto;
import semillero.ecosistema.entity.PublicationEntity;
import semillero.ecosistema.entity.UserEntity;
import semillero.ecosistema.exception.PublicationExistException;
import semillero.ecosistema.exception.PublicationNotExistException;
import semillero.ecosistema.exception.UserNotExistException;
import semillero.ecosistema.mapper.PublicationMapper;
import semillero.ecosistema.repository.PublicationRepository;
import semillero.ecosistema.repository.UserRepository;
import semillero.ecosistema.service.contracts.PublicationService;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationServiceImpl  implements PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<PublicationEntity> publications = publicationRepository.findAll();
            List<PublicationResponseDto> publicationsDto = publications.stream()
                    .map(publicationMapper::toResponseDto)
                    .collect(Collectors.toList());

            if(publicationsDto.isEmpty()){
                List<?> listnull = new ArrayList<>();
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(publicationsDto);
        } catch (Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> getByTitle(String title){
        try {
            PublicationEntity publication = publicationRepository.findByTitle(title);
            if (publication == null) {
                throw new PublicationNotExistException();
            }
            PublicationResponseDto publicationDto = publicationMapper.toResponseDto(publication);
            return ResponseEntity.ok(publicationDto);
        }catch (PublicationNotExistException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The publication does not exist with this title: " + title);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }
    }

    @Override
    public ResponseEntity<?> getById(String id){
        try {
            PublicationEntity publication = publicationRepository.findById(id)
                    .orElseThrow(PublicationNotExistException::new);
            PublicationResponseDto publicationDto = publicationMapper.toResponseDto(publication);
            return ResponseEntity.ok(publicationDto);
        }catch (PublicationNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The publication does not exist with this ID: " + id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }

    }

    @Override
    public ResponseEntity<?> getByDeletedFalse(){
        try {
            List<PublicationEntity> publications = publicationRepository.findByHiddenFalse();
            List<PublicationResponseDto> publicationsDto = publications.stream()
                    .map(publicationMapper::toResponseDto)
                    .collect(Collectors.toList());
            if(publicationsDto.isEmpty()){
                List<?> listnull = new ArrayList<>();
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(listnull);
            }
            return ResponseEntity.ok(publicationsDto);
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> getByUsuarioId(String user_id){
        try {
            List<PublicationEntity> publications = publicationRepository.findByUsuarioCreadorId(user_id);
            List<PublicationResponseDto> publicationsDto = publications.stream()
                    .map(publicationMapper::toResponseDto)
                    .collect(Collectors.toList());
            if(publicationsDto.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(publicationsDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }

    }

    @Override
    public void incrementViewCount(String id){
            PublicationEntity publication = publicationRepository.findById(id)
                    .orElseThrow(PublicationNotExistException::new);
            publication.setVisualizations(publication.getVisualizations() + 1);
            publicationRepository.save(publication);
    }

    @Override
    public ResponseEntity<?> save(PublicationRequestDto publicationRequestDto){
        try {
            UserEntity user = userRepository.findById(publicationRequestDto.getUser_id())
                    .orElseThrow(UserNotExistException::new);
            PublicationEntity publication = publicationMapper.toEntity(publicationRequestDto);
            publication.setUsuarioCreador(user);
            publicationRepository.save(publication);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (UserNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("User ID not found");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> update(PublicationRequestDto publicationRequestDto){
        try {
            PublicationEntity publication = publicationRepository.findById(publicationRequestDto.getId())
                    .orElseThrow(PublicationNotExistException::new);

            List<String> images = new ArrayList<>();
            publication.setTitle(publicationRequestDto.getTitle());
            publication.setContent(publicationRequestDto.getContent());
            publication.setDate(publicationRequestDto.getDate());
            publication.setImages(images);
            publication.setVisualizations(publicationRequestDto.getVisualizations());

            publicationRepository.save(publication);

            return ResponseEntity.ok().body("UPDATED");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }



    }

    @Override
    public ResponseEntity<?> delete(String id){
        try {
            PublicationEntity publication = publicationRepository.findById(id)
                    .orElseThrow(PublicationNotExistException::new);
            publication.setHidden(true);
            publicationRepository.save(publication);
            return ResponseEntity.status(HttpStatus.OK).body("HIDDEN");
        }catch (PublicationNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication Not Found");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public ResponseEntity<?> active(String id){
        try {
            PublicationEntity publication = publicationRepository.findById(id)
                    .orElseThrow(PublicationNotExistException::new);
            publication.setHidden(false);
            publicationRepository.save(publication);
            return ResponseEntity.status(HttpStatus.OK).body("ACTIVED");
        }catch (PublicationNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
