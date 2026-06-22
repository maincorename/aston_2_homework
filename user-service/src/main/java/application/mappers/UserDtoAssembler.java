package application.mappers;

import application.controllers.UserController;
import application.dto.UserDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserDtoAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {
    @Override
    public EntityModel<UserDto> toModel(UserDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(dto.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"),
                linkTo(methodOn(UserController.class).updateUser(dto.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(dto.id())).withRel("delete"));
    }

    public CollectionModel<EntityModel<UserDto>> toCollectionModel(List<UserDto> dtos) {
        List<EntityModel<UserDto>> userModels = dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(userModels, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }
}
