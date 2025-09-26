package co.edu.uniajc.estudiante.opemay.dto;

/**
 * DTO para crear una nueva categoría
 */
public class CategoryCreateDTO {
    private String name;
    private String description;
    private String slug;

    // Constructors
    public CategoryCreateDTO() {}

    public CategoryCreateDTO(String name, String description, String slug) {
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