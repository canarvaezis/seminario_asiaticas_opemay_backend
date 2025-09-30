package co.edu.uniajc.estudiante.opemay.dto;

/**
 * DTO para actualizar una categoría
 */
public class CategoryUpdateDTO {
    private String name;
    private String description;
    private String slug;

    // Constructors
    public CategoryUpdateDTO() {}

    public CategoryUpdateDTO(String name, String description, String slug) {
        this.name = name;
        this.description = description;
        this.slug = slug;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}