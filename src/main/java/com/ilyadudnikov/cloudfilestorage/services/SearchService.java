package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.MinioObjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final FileService fileService;

    public List<MinioObjectDto> search(String query, long ownerId) {
        List<MinioObjectDto> allUserObjects = fileService.getAllUserFilesInFolder(ownerId, "");

        List<MinioObjectDto> foundFilesAndFolders = allUserObjects.stream() // Здесь будут только созданные папки
                .filter(object -> object.getName().contains(query))
                .toList();

        Set<MinioObjectDto> foundFolders = findMatchingFolders(query, allUserObjects);

        foundFolders.addAll(foundFilesAndFolders);

        return new ArrayList<>(foundFolders);
    }

    private Set<MinioObjectDto> findMatchingFolders(String query, List<MinioObjectDto> allUserObjects) {
        Set<MinioObjectDto> matchingFolders = new HashSet<>();
        for (MinioObjectDto object : allUserObjects) {
            String path = object.getPath();
            String[] parts = path.split("/");
            StringBuilder currentPath = new StringBuilder();

            for (String part : parts) {
                if (part.contains(query)) {
                    matchingFolders.add(
                            new MinioObjectDto(part, true, currentPath.toString())
                    );
                }
                currentPath.append(part).append("/");
            }
        }

        return matchingFolders;
    }
}
