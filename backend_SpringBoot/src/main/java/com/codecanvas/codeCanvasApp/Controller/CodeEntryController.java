package com.codecanvas.codeCanvasApp.Controller;

import com.codecanvas.codeCanvasApp.Repository.CodeEntryRepository;
import com.codecanvas.codeCanvasApp.Repository.UserRepository;
import com.codecanvas.codeCanvasApp.Service.CodeEntryService;
import com.codecanvas.codeCanvasApp.Service.UserService;
import com.codecanvas.codeCanvasApp.entity.CodeEntry;
import com.codecanvas.codeCanvasApp.entity.Comment;
import com.codecanvas.codeCanvasApp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/codeEntry")
@Slf4j
public class CodeEntryController {

    @Autowired
    private CodeEntryService codeEntryService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeEntryRepository codeEntryRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<CodeEntry> entriesPage = codeEntryRepository.findByIsPublicTrue(pageable);

        return new ResponseEntity<>(entriesPage, HttpStatus.OK);
    }



    @GetMapping("/entries")
    public ResponseEntity<?> getallEntriesofUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User userInDb = userRepository.findByUserName(username);


        if (userInDb.getIsActive() == null || !userInDb.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is deactivated. You cannot create entries.");
        }
        try {
            List<CodeEntry> all = userInDb.getCodeEntries();
            if (!all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }

   /* @GetMapping("/Recent-entries")
    public ResponseEntity<?> getEntriesForFeed() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User userInDb = userRepository.findByUserName(username);


        if (userInDb.getIsActive() == null || !userInDb.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is deactivated. You cannot create entries.");
        }
        try {
            List<CodeEntry> recentEntries = CodeEntryRepository.findAll();
            if (!all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }*/
    @GetMapping("/public-entries")
    public ResponseEntity<?> getPublicEntries(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username= auth.getName();
        User userInDb=userRepository.findByUserName(username);
        if (userInDb.getIsActive() == null || !userInDb.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is deactivated. You cannot create entries.");
        }
        try{
            List<CodeEntry> all = userInDb.getCodeEntries();

            List<CodeEntry> publicEntries = all.stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.getIsPublic()))
                    .toList();
            if (!publicEntries.isEmpty()) {
                System.out.println("Display filtered public entreis");
                return new ResponseEntity<>(publicEntries, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Either no entries or no public entries from this user yet", HttpStatus.OK);
            }

        }catch (Exception e) {
            log.error("Error Occured bro");
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }




    }

    @GetMapping("/private-entries")
    public ResponseEntity<?> getPrivateEntries(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username= auth.getName();
        User userInDb=userRepository.findByUserName(username);
        if (userInDb.getIsActive() == null || !userInDb.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is deactivated. You cannot create entries.");
        }
        try{
            List<CodeEntry> all = userInDb.getCodeEntries();

            List<CodeEntry> privateEntries = all.stream()
                    .filter(entry -> Boolean.FALSE.equals(entry.getIsPublic()))
                    .toList();
            if (!privateEntries.isEmpty()) {
                System.out.println("Display filtered public entreis");
                return new ResponseEntity<>(privateEntries, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No private entries from this user yet", HttpStatus.OK);
            }

        }catch (Exception e) {

            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }




    }

    @PostMapping()
    public ResponseEntity<?> createNewEntry(@RequestBody CodeEntry codeEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);

        if (user.getIsActive() == null || !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is deactivated. You cannot create entries.");
        }

        try {
            codeEntryService.saveEntry(codeEntry, userName);

            return new ResponseEntity<>(codeEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("/entryId/{myid}")
    public ResponseEntity<?> getCodeEntryById(@PathVariable ObjectId myid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Optional<CodeEntry> myEntry = codeEntryService.findById(myid);// Ye ek optional conatiner hai jisme codeEntry ho bhi skta hai aur nhi bhi

        if (myEntry.isPresent()) { //Abhi bhi myEntry ek conatiner hai aur hmmko usme se extract krna hai

            CodeEntry entry = myEntry.get();//This gets the actual CodeEntry object from inside the Optional.


            if (entry.getIsPublic() != null && entry.getIsPublic()) {
                return new ResponseEntity<>(entry, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Entry is private", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

    @GetMapping("/authorId/{myauthorId}")
    public ResponseEntity<?> getEntriesByAuthorId(@PathVariable String myauthorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            ObjectId myId = new ObjectId(myauthorId);
            Optional<User> author = userRepository.findById(myId);

            if (author.isEmpty()) {
                return new ResponseEntity<>("No authors with this id", HttpStatus.NOT_FOUND);
            }
            if (author.get().getIsActive() == null || !author.get().getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authors account is currently deactivated");
            }

            List<CodeEntry> all = codeEntryService.findByAuthorId(myauthorId);

            List<CodeEntry> publicEntries = all.stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.getIsPublic()))
                    .toList();


            if (!publicEntries.isEmpty()) {
                return new ResponseEntity<>(publicEntries, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Either no entries or no public entries from this user yet", HttpStatus.OK);
            }

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid ObjectId format", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getEntriesByUserName(@PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userInDb = userRepository.findByUserName(username);
        System.out.println("Request for" +  userInDb);

        try {
            if (userInDb == null) {
                System.out.println(  "userInDb is null");
                return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
            } else {
                if (userInDb.getIsActive() == null || !userInDb.getIsActive()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Users account is currently deactivated");
                } else {
                    System.out.println("Inside else block");
                    List<CodeEntry> all = userInDb.getCodeEntries();
                    System.out.println("Got all entries");

                    List<CodeEntry> publicEntries = all.stream()
                            .filter(entry -> Boolean.TRUE.equals(entry.getIsPublic()))
                            .toList();

                    System.out.println("Filtered public entries");

                    if (!all.isEmpty()) {
                        System.out.println("Display filtered public entreis");
                        return new ResponseEntity<>(publicEntries, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Either no entries or no public entries from this user yet", HttpStatus.OK);
                    }

                }
            }
        } catch (Exception e) {
            log.error("Error Occured bro");
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/savedEntries")
    public ResponseEntity<?> getAllSavedEntries(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        User user=userRepository.findByUserName(username);
        if (user.getIsActive() == null || !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Users account is currently deactivated");
        }

        List<CodeEntry> savedEntries=user.getSavedEntries();

        if(!savedEntries.isEmpty()){
            return new ResponseEntity<>(savedEntries,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @GetMapping("/tags")
    public ResponseEntity<?> getEntriesByTags(@RequestParam List<String> tags) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        List<String> normalizedTags = tags.stream()
                .map(tag -> tag.toLowerCase().trim())
                .distinct()
                .collect(Collectors.toList());

        List<CodeEntry> entries = codeEntryRepository.findByIsPublicTrueAndTagsIn(normalizedTags);
        try {
            if (entries.isEmpty()) {
                return new ResponseEntity<>("No public entries with such tags", HttpStatus.NOT_FOUND);
            } else {
                entries.sort((a, b) -> {
                    long bMatches = b.getTags().stream().filter(normalizedTags::contains).count();
                    long aMatches = a.getTags().stream().filter(normalizedTags::contains).count();
                    return Long.compare(bMatches, aMatches);
                });
                return new ResponseEntity<>(entries, HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }


    @DeleteMapping("/entryId/{id}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();


        User user = userRepository.findByUserName(username);

        if (user.getIsActive() == null || !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is currently deactivated");
        }


        Optional<CodeEntry> byId = codeEntryRepository.findById(id);

        if (byId.isEmpty()) {
            return new ResponseEntity<>("No such existing entry", HttpStatus.NOT_FOUND);
        }


        String authorid = byId.get().getAuthorId();


        if (!user.getId().toString().equals(authorid)) {
            return new ResponseEntity<>("You can't delete someones else entry", HttpStatus.FORBIDDEN);
        }


        try {


            codeEntryService.deleteEntry(id);
            user.getCodeEntries().removeIf(entry -> entry.getId().equals(id));
            userRepository.save(user);

            return new ResponseEntity<>("Successfully Deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An Error occured while deleting the entry", HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/entryId/{myId}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId myId, @RequestBody CodeEntry newEntry) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUserName(username);


        if (user.getIsActive() == null || !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is currently deactivated");
        }

        Optional<CodeEntry> codeEntryById = codeEntryRepository.findById(myId);

        if (codeEntryById.isEmpty()) {
            return new ResponseEntity<>("No such existing entry", HttpStatus.NOT_FOUND);
        }

        String authorid = codeEntryById.get().getAuthorId();


        if (!user.getId().toString().equals(authorid)) {
            return new ResponseEntity<>("You can't update someones else entry", HttpStatus.FORBIDDEN);
        }

        try {
            CodeEntry old = codeEntryById.get();
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());
            old.setDescription(newEntry.getDescription() != null && !newEntry.getDescription().equals("") ? newEntry.getDescription() : old.getDescription());
            if (newEntry.getIsPublic() != null) {
                old.setIsPublic(newEntry.getIsPublic());
            }
            codeEntryService.saveEntry(old, username);
            return new ResponseEntity<>("Your entry is successfully updated", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Code cant be updated", HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping("/save/{entryId}")
    public ResponseEntity<?> saveEntryById(@PathVariable ObjectId entryId) {
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        String username = aut.getName();
        User user = userRepository.findByUserName(username);

        if(user.getIsActive()==null || !user.getIsActive()){
            return new ResponseEntity<>("Your id is currently deactivated",HttpStatus.FORBIDDEN);
        }

        Optional<CodeEntry> entry = codeEntryRepository.findById(entryId);
        if (entry.isEmpty()) {
            return new ResponseEntity<>("No such entry with this id exists", HttpStatus.BAD_REQUEST);
        }
        CodeEntry myEntry = entry.get();

        if(entry.get().getAuthorId().equals(user.getId().toString())){
            List<CodeEntry> saved = user.getSavedEntries();
            saved.add(myEntry);
            userRepository.save(user);


            return new ResponseEntity<>("Your own entry is saved",HttpStatus.OK);
        }
        if(myEntry.getIsPublic()==null || !myEntry.getIsPublic() ){
            return new ResponseEntity<>("You can't save someones's else private entry",HttpStatus.FORBIDDEN);
        }

        try {

            List<CodeEntry> saved = user.getSavedEntries();
            if(saved.contains(myEntry)){
                return new ResponseEntity<>("Already saved in your saved list",HttpStatus.CONFLICT);
            }
            saved.add(myEntry);
            userRepository.save(user);
            return new ResponseEntity<>("Added entry in your saved List",HttpStatus.OK);


        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @DeleteMapping("/unsaveEntry/{entryId}")
    public ResponseEntity<?> unsaveEntry(@PathVariable ObjectId entryId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        User user=userRepository.findByUserName(username);
        if(user.getIsActive()==null || !user.getIsActive()){
            return new ResponseEntity<>("Your id is currently deactivated",HttpStatus.FORBIDDEN);
        }

        Optional<CodeEntry> box=codeEntryRepository.findById(entryId);
        if(box.isEmpty()){
            return new ResponseEntity<>("No such entry  in database",HttpStatus.NOT_FOUND);
        }
        CodeEntry myEntry=box.get();
        if(user.getSavedEntries().contains(myEntry)){
            List<CodeEntry> savedEntries=user.getSavedEntries();
            savedEntries.remove(myEntry);
            userRepository.save(user);
            return new ResponseEntity<>("Removed entry from saved list",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("No such entry exists in your saves list",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/entryid/{entryId}/comment")
    public ResponseEntity<?> commenting(@PathVariable ObjectId entryId, @RequestBody String myComment){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();
        User user=userRepository.findByUserName(username);

        if(user.getIsActive()==null || !user.getIsActive()){
            return new ResponseEntity<>("Your id is currently deactivated",HttpStatus.FORBIDDEN);
        }
        Optional<CodeEntry> box=codeEntryRepository.findById(entryId);

        if(box.isEmpty()){
            return new ResponseEntity<>("No such entry exists",HttpStatus.NOT_FOUND);
        }

        CodeEntry entry=box.get();

        Comment comment=new Comment();
        comment.setCommenterId(user.getId());
        comment.setCommenterUsername(user.getUserName());
        comment.setText(myComment);
        comment.setCommentedAt(LocalDateTime.now());

        entry.getComments().add(comment);
        codeEntryRepository.save(entry);

        return new ResponseEntity<>("Your comment is added",HttpStatus.OK);




    }

    @GetMapping("/entryId/{entryId}/comments")
    public ResponseEntity<?> getComments(@PathVariable ObjectId entryId) {
        Optional<CodeEntry> entry = codeEntryRepository.findById(entryId);
        if (entry.isEmpty()) {
            return new ResponseEntity<>("No such entry found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entry.get().getComments(), HttpStatus.OK);
    }

    @PostMapping("/{entryId}/like-toggle")
    public ResponseEntity<?> toggleLike(@PathVariable ObjectId entryId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUserName(username);

        if (user.getIsActive() == null || !user.getIsActive()) {
            return new ResponseEntity<>("Your id is currently deactivated", HttpStatus.FORBIDDEN);
        }

        Optional<CodeEntry> entryOpt = codeEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return new ResponseEntity<>("No such entry found", HttpStatus.NOT_FOUND);
        }

        CodeEntry entry = entryOpt.get();

        boolean alreadyLiked = entry.getLikedByUserIds().contains(user.getId());
        if (alreadyLiked) {
            entry.getLikedByUserIds().remove(user.getId());
        } else {
            entry.getLikedByUserIds().add(user.getId());
        }

        codeEntryRepository.save(entry);

        return new ResponseEntity<>(
                alreadyLiked ? "Unliked successfully" : "Liked successfully",
                HttpStatus.OK
        );
    }

    @GetMapping("/{entryId}/likes/count")
    public ResponseEntity<?> getLikeCount(@PathVariable ObjectId entryId) {
        Optional<CodeEntry> entryOpt = codeEntryRepository.findById(entryId);
        if (entryOpt.isEmpty()) {
            return new ResponseEntity<>("No such entry found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entryOpt.get().getLikedByUserIds().size(), HttpStatus.OK);
    }






}