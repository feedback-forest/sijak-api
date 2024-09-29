package zerobase.sijak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoInfo {
    public Meta meta;
    public List<Document> documents;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        public int total_count;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        public RoadAddress road_address;
        public Address address;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadAddress {
        public String address_name;
        public String region_1depth_name;
        public String region_2depth_name;
        public String region_3depth_name;
        public String road_name;
        public String main_building_no;
        public String building_name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        public String address_name;
        public String region_1depth_name;
        public String region_2depth_name;
        public String region_3depth_name;
        public String main_address_no;
        public String sub_address_no;
    }
}

