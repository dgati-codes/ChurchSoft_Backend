package com.churchsoft.users.controller;

import com.churchsoft.global.dto.AccountType;
import com.churchsoft.users.entity.ImageFile;
import com.churchsoft.users.service.ImageService;
import com.churchsoft.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/church-soft/v1.0/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    @Operation(
            summary = "Upload an image",
            description = """
                    Uploads an image file and stores it in the database.
                    Returns the generated image ID which can be assigned to a user
                    (e.g., profile picture).
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Image uploaded successfully and image ID returned"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image file or file size exceeds limit"
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadImage(
            @RequestPart("image") MultipartFile image
    ) throws IOException {

        return ResponseEntity.ok(imageService.create(image));
    }

    @Operation(
            summary = "Get image by ID",
            description = """
                    Retrieves the raw image bytes using the image ID.
                    This endpoint is typically used to render images in the browser
                    or frontend applications.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Image retrieved successfully",
                    content = @Content(mediaType = "image/*")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable Long id) {

        ImageFile image = imageService.get(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @Operation(
            summary = "Update an existing image",
            description = """
                    Replaces an existing image with a new one while retaining
                    the same image ID. Commonly used when updating a user's
                    profile picture.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Image updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image file or file size exceeds limit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image not found"
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image
    ) throws IOException {

        imageService.update(id, image);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete an image",
            description = """
                    Deletes an image permanently from the system.
                    Once deleted, the image ID can no longer be used
                    or assigned to a user.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Image deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Assign image to user",
            description = """
                    Assigns an existing image (by image ID) to a user.
                    This is used to set or update a user's profile image.
                    Pass imageId as 0 to remove the currently assigned image.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image assigned to user successfully"),
            @ApiResponse(responseCode = "404", description = "User or image not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("/assign-image/{accountType}/{accountId}/{imageId}")
    public ResponseEntity<Void> assignImage(
            @PathVariable AccountType accountType,
            @PathVariable Long accountId,
            @PathVariable Long imageId
    ) {
        userService.assignImage(accountType, accountId, imageId);
        return ResponseEntity.noContent().build();
    }
}
