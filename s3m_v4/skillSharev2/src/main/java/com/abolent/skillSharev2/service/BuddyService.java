package com.abolent.skillSharev2.service;



import com.abolent.skillSharev2.model.Buddy;
import com.abolent.skillSharev2.repository.BuddyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BuddyService {

    @Autowired
    private BuddyRepository buddyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerBuddy(Buddy buddy) {
        buddy.setPassword(passwordEncoder.encode(buddy.getPassword()));
        buddyRepository.save(buddy);
    }
}
