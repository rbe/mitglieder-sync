/*
 * mitglieder-server
 * mitglieder-api
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 * Copyright (C) 2008-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.12.12 17:54
 */

package eu.artofcoding.mitglieder.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "profile_fields", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@NamedQueries({
        @NamedQuery(name = "ProfileFields.findAll", query = "SELECT p FROM ProfileFields p"),
        @NamedQuery(name = "ProfileFields.findByFid", query = "SELECT p FROM ProfileFields p WHERE p.fid = :fid"),
        @NamedQuery(name = "ProfileFields.findByTitle", query = "SELECT p FROM ProfileFields p WHERE p.title = :title"),
        @NamedQuery(name = "ProfileFields.findByName", query = "SELECT p FROM ProfileFields p WHERE p.name = :name"),
        @NamedQuery(name = "ProfileFields.findByCategory", query = "SELECT p FROM ProfileFields p WHERE p.category = :category"),
        @NamedQuery(name = "ProfileFields.findByPage", query = "SELECT p FROM ProfileFields p WHERE p.page = :page"),
        @NamedQuery(name = "ProfileFields.findByType", query = "SELECT p FROM ProfileFields p WHERE p.type = :type"),
        @NamedQuery(name = "ProfileFields.findByWeight", query = "SELECT p FROM ProfileFields p WHERE p.weight = :weight"),
        @NamedQuery(name = "ProfileFields.findByRequired", query = "SELECT p FROM ProfileFields p WHERE p.required = :required"),
        @NamedQuery(name = "ProfileFields.findByRegister", query = "SELECT p FROM ProfileFields p WHERE p.register = :register"),
        @NamedQuery(name = "ProfileFields.findByVisibility", query = "SELECT p FROM ProfileFields p WHERE p.visibility = :visibility"),
        @NamedQuery(name = "ProfileFields.findByAutocomplete", query = "SELECT p FROM ProfileFields p WHERE p.autocomplete = :autocomplete")})
public class ProfileFields implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "fid", nullable = false)
    private Integer fid;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "name", length = 128)
    private String name;

    @Lob
    @Column(name = "explanation", length = 65535)
    private String explanation;

    @Column(name = "category", length = 255)
    private String category;

    @Column(name = "page", length = 255)
    private String page;

    @Column(name = "type", length = 128)
    private String type;

    @Basic(optional = false)
    @Column(name = "weight", nullable = false)
    private short weight;

    @Basic(optional = false)
    @Column(name = "required", nullable = false)
    private short required;

    @Basic(optional = false)
    @Column(name = "register", nullable = false)
    private short register;

    @Basic(optional = false)
    @Column(name = "visibility", nullable = false)
    private short visibility;

    @Basic(optional = false)
    @Column(name = "autocomplete", nullable = false)
    private short autocomplete;

    @Lob
    @Column(name = "options", length = 65535)
    private String options;

    public ProfileFields() {
    }

    public ProfileFields(Integer fid) {
        this.fid = fid;
    }

    public ProfileFields(Integer fid, short weight, short required, short register, short visibility, short autocomplete) {
        this.fid = fid;
        this.weight = weight;
        this.required = required;
        this.register = register;
        this.visibility = visibility;
        this.autocomplete = autocomplete;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public short getWeight() {
        return weight;
    }

    public void setWeight(short weight) {
        this.weight = weight;
    }

    public short getRequired() {
        return required;
    }

    public void setRequired(short required) {
        this.required = required;
    }

    public short getRegister() {
        return register;
    }

    public void setRegister(short register) {
        this.register = register;
    }

    public short getVisibility() {
        return visibility;
    }

    public void setVisibility(short visibility) {
        this.visibility = visibility;
    }

    public short getAutocomplete() {
        return autocomplete;
    }

    public void setAutocomplete(short autocomplete) {
        this.autocomplete = autocomplete;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fid != null ? fid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProfileFields)) {
            return false;
        }
        ProfileFields other = (ProfileFields) object;
        if ((this.fid == null && other.fid != null) || (this.fid != null && !this.fid.equals(other.fid))) {
            return false;
        }
        return true;
    }

}
