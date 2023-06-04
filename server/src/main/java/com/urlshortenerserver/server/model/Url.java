package com.urlshortenerserver.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "url")
    private String url;

    @Column(name = "code")
    private String code;

    /**
     * Getter for ıd
     * @return {*Long} id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for Id
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for Url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter for Url
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for Code
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter for Code
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }
}