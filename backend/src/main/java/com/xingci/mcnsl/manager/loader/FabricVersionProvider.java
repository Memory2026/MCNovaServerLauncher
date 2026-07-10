package com.xingci.mcnsl.manager.loader;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingci.mcnsl.model.LoaderVersionInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Component
public class FabricVersionProvider {


    private static final String FABRIC_API_URL =
            "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml";


    private final RestTemplate rest =
            new RestTemplate();


    private final ObjectMapper mapper =
            new ObjectMapper();


    public List<LoaderVersionInfo> findVersions(
            String mcVersion
    ){

        List<LoaderVersionInfo> list =
                new ArrayList<>();


        String recommendedFabricApi =
                findRecommendedFabricApi(mcVersion);


        try {


            String url =
                    "https://meta.fabricmc.net/v2/versions/loader/"
                            + mcVersion;


            String json =
                    rest.getForObject(
                            url,
                            String.class
                    );


            if(json == null)
                return list;


            JsonNode array =
                    mapper.readTree(json);


            for(JsonNode node:array){


                JsonNode loader =
                        node.get("loader");


                String version =
                        loader.get("version").asText();


                boolean stable =
                        loader.has("stable")
                                && loader.get("stable").asBoolean();


                String releaseTime =
                        loader.has("date")
                                ? loader.get("date").asText()
                                : "";


                LoaderVersionInfo info =
                        new LoaderVersionInfo(
                                "fabric",
                                version,
                                mcVersion
                        );


                info.setRecommended(stable);
                info.setFabricApiVersion(recommendedFabricApi);
                info.setVersionType(stable ? "stable" : "beta");
                info.setReleaseTime(releaseTime);


                list.add(info);


            }


        }catch(Exception e){

            e.printStackTrace();

        }


        list.sort((v1, v2) -> {
            int[] arr1 = parseVersion(v1.getVersion());
            int[] arr2 = parseVersion(v2.getVersion());
            for (int i = 0; i < 3; i++) {
                if (arr1[i] != arr2[i]) {
                    return Integer.compare(arr2[i], arr1[i]);
                }
            }
            return 0;
        });


        return list;

    }


    private String findRecommendedFabricApi(
            String mcVersion
    ){

        try {


            String xml =
                    rest.getForObject(
                            FABRIC_API_URL,
                            String.class
                    );


            if(xml == null)
                return null;


            List<String> versions =
                    new ArrayList<>();


            String[] parts =
                    xml.split("<version>");


            for(String v:parts){


                if(v.contains("</version>")){


                    String version =
                            v.substring(
                                    0,
                                    v.indexOf("</version>")
                            ).trim();


                    if(isValidFabricApiVersion(version, mcVersion)){


                        versions.add(version);


                    }

                }

            }


            if(versions.isEmpty()){


                String majorMinor = mcVersion;
                if(majorMinor.contains(".")){
                    String[] split = majorMinor.split("\\.");
                    if(split.length >= 2){
                        majorMinor = split[0] + "." + split[1];
                    }
                }


                for(String v:parts){
                    if(v.contains("</version>")){
                        String version = v.substring(0, v.indexOf("</version>")).trim();
                        if(version.contains("+" + majorMinor + ".") || version.contains("-" + majorMinor + ".")){
                            if(!versions.contains(version)){
                                versions.add(version);
                            }
                        }
                    }
                }
            }


            versions.sort(Comparator.reverseOrder());


            return versions.isEmpty() ? null : versions.get(0);


        }catch(Exception e){

            e.printStackTrace();

            return null;

        }

    }

    private boolean isValidFabricApiVersion(String version, String mcVersion) {
        if (!version.contains("+") && !version.contains("-")) {
            return false;
        }
        String[] versionParts = version.split("[+-]");
        if (versionParts.length < 2) {
            return false;
        }
        String suffix = version.substring(version.indexOf(versionParts[versionParts.length - 1]));
        return suffix.startsWith(mcVersion)
                || suffix.startsWith("build." + mcVersion)
                || suffix.contains("-" + mcVersion)
                || suffix.contains("+" + mcVersion);
    }

    private int[] parseVersion(String version) {
        if (version == null || version.isBlank()) {
            return new int[]{0, 0, 0};
        }
        String[] parts = version.split("\\.");
        int[] result = new int[3];
        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            try {
                result[i] = Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

}