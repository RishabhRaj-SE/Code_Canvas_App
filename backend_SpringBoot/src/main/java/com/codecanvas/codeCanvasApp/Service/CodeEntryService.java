package com.codecanvas.codeCanvasApp.Service;

import com.codecanvas.codeCanvasApp.Repository.CodeEntryRepository;
import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.entity.CodeEntry;
import com.codecanvas.codeCanvasApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Service
public class CodeEntryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeEntryRepository codeEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SmartTaggingService smartTaggingService;



    public void saveEntry(CodeEntry codeEntry,String userName) {
        User user = userRepository.findByUserName(userName);
        if(codeEntry.getId()==null) {
            // Always generate AI tags
            List<String> aiTags = smartTaggingService.generateTags(codeEntry.getContent());

            // If user already gave tags, merge them
            List<String> finalTags = new ArrayList<>();
            if (codeEntry.getTags() != null && !codeEntry.getTags().isEmpty()) {
                finalTags.addAll(codeEntry.getTags());
            }
            finalTags.addAll(aiTags);

            // Remove duplicates + normalize
            finalTags = finalTags.stream()
                    .map(tag -> tag.toLowerCase().trim())
                    .distinct()
                    .collect(Collectors.toList());

            codeEntry.setTags(finalTags);
            codeEntry.setDate(LocalDateTime.now());
            codeEntry.setAuthorId(user.getId().toString());
            codeEntry.setAuthorName(userName);
            CodeEntry saved = codeEntryRepository.save(codeEntry);//Yha se mjhe wo database me saved entry mila jiska database me ek id hai
            user.getCodeEntries().add(saved);
            userService.entryUpdatedsaveUser(user);
        }
        codeEntryRepository.save(codeEntry);

    }

  /*  public void saveUpdatedEntry(CodeEntry codeEntry){

    }*/








    public Optional<CodeEntry> findById(ObjectId myid){// Ye optional return type hai qki mongodb ka already defined function optional return krta hai
        return codeEntryRepository.findById(myid);
    }

    public List<CodeEntry> findByAuthorId(String authorId){// ye hamara manual repo function hai isme optional allowed nhi
        return codeEntryRepository.findByauthorId(authorId);
    }
    public void deleteEntry(ObjectId id){

            codeEntryRepository.deleteById(id);

    }


}
