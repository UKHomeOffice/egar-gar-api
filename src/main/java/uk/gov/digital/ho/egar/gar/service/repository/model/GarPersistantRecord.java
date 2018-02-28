package uk.gov.digital.ho.egar.gar.service.repository.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;
import uk.gov.digital.ho.egar.shared.util.persistence.adapters.LocalDateTimeConverter;

/**
 * The data for a GAR and its list of associated
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "FlightGar",
        indexes = @Index(name = "userIdx", columnList = "userUuid"))
@Validated
public class GarPersistantRecord implements Gar {

    @Id
    @NotNull
    private UUID garUuid;

    @NotNull
    private UUID userUuid;

    private UUID aircraftUuid;

    @JsonIgnore
    private UUID captainUuid;

    @JsonIgnore
    @Valid
    @OneToMany(targetEntity = LocationUuidPersistedRecord.class, fetch = FetchType.LAZY, mappedBy = "garUuid", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("locationOrder asc")
    private List<UuidPersistedRecord> locations;

    @JsonIgnore
    @Valid
    @OneToMany(targetEntity = CrewUuidPersistedRecord.class, fetch = FetchType.LAZY, mappedBy = "garUuid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UuidPersistedRecord> crew;

    @JsonIgnore
    @Valid
    @OneToMany(targetEntity = PassengerUuidPersistedRecord.class, fetch = FetchType.LAZY, mappedBy = "garUuid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UuidPersistedRecord> passengers;

    @JsonIgnore
    @Valid
    @OneToMany(targetEntity = SupportingFileUuidPersistedRecord.class,
            fetch = FetchType.LAZY,
            mappedBy = "garUuid",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UuidPersistedRecord> files;

    @JsonIgnore
    @Valid
    @OneToMany(targetEntity = AttributePersistedRecord.class,
            fetch = FetchType.LAZY, mappedBy = "garUuid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributePersistedRecord> attributes;

    @JsonIgnore
    @Column
    @Convert(converter = LocalDateTimeConverter.class)
    @Valid
    private LocalDateTime lastModified;
    
    @JsonIgnore
    @Column
    @Convert(converter = LocalDateTimeConverter.class)
    @Valid
    private LocalDateTime created;

    private UUID submissionUuid;

    public GarPersistantRecord updateLastModified() {
        this.lastModified = LocalDateTime.now();
        return this;
    }


    private List<UUID> returnUuids(List<UuidPersistedRecord> uuidList) {

        if (uuidList == null) return null;
        if (uuidList.isEmpty()) return null;

        ArrayList<UUID> retList = new ArrayList<UUID>(uuidList.size());

        for (UuidPersistedRecord record : uuidList) {
            retList.add(record.getValueUuid());
        }

        return retList;
    }


    @Override
    @Transient
    public PeopleOnGar getPeopleOnGar() {
        return new PeopleOnGar() {

            @Override
            public UUID getCaptain() {
                return GarPersistantRecord.this.getCaptainUuid();
            }


            @Override
            public List<UUID> getCrew() {
                return returnUuids(GarPersistantRecord.this.getCrew());
            }


            @Override
            public List<UUID> getPassengers() {
                return returnUuids(GarPersistantRecord.this.getPassengers());
            }

        };
    }

    @Override
    @Transient
    public List<UUID> getFileUuids() {
        return returnUuids(this.getFiles());
    }

    @Override
    @Transient
    public Map<String, String> getAttributeMap() {

        List<AttributePersistedRecord> internalAttributes = this.getAttributes();

        if (internalAttributes == null) return null;
        if (internalAttributes.isEmpty()) return null;

        Map<String, String> retMap = new HashMap<String, String>(internalAttributes.size());

        for (AttributePersistedRecord record : internalAttributes) {
            retMap.put(record.getName(), record.getValue());
        }

        return retMap;
    }

    @Override
    @Transient
    public List<UUID> getLocationUuids() {
        return returnLocationUuids(this.getLocations());
    }

    /**
     * Returns a list of location UUIDs.
     * This will always return 2 locations which will default to null.
     *
     * @param locationUuidList The location presisted record list
     * @return The location uuids
     */
    private List<UUID> returnLocationUuids(List<UuidPersistedRecord> locationUuidList) {

        ArrayList<UUID> retList = new ArrayList<UUID>();
        retList.add(null);
        retList.add(null);

        if (locationUuidList == null) return retList;
        if (locationUuidList.isEmpty()) return retList;

        for (UuidPersistedRecord record : locationUuidList) {
            if (record instanceof LocationUuidPersistedRecord) {
                LocationUuidPersistedRecord location = (LocationUuidPersistedRecord) record;
                retList.set(location.getLocationOrder() - 1, record.getValueUuid());
            }
        }

        return retList;
    }
}
