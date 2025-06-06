package com.rpg_game.game.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg_game.game.entity.Ability;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.entity.Effect;
import com.rpg_game.game.repositories.AbilityRepository;
import com.rpg_game.game.repositories.CharacterRepository;
import com.rpg_game.game.repositories.EffectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataLoader implements CommandLineRunner {

    

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final CharacterRepository characterRepository;
    private final AbilityRepository abilityRepository;
    private final EffectRepository effectRepository;
    private final ObjectMapper objectMapper;

    public DataLoader(CharacterRepository characterRepository,
                      AbilityRepository abilityRepository,
                      EffectRepository effectRepository,
                      ObjectMapper objectMapper) { // ObjectMapper is provided by Spring Boot
        this.characterRepository = characterRepository;
        this.abilityRepository = abilityRepository;
        this.effectRepository = effectRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional // Ensures all database operations for one run are in a single transaction
    public void run(String... args) throws Exception {
        // Prevent re-loading data if characters already exist
        System.out.println("Data Loader Invoked ------------------------------------------>>>>>>>>>>");
        if (characterRepository.count() > 0) {
            logger.info("Database already contains characters. Skipping data loading.");
            return;
        }

        logger.info("Loading character data from JSON...");

        try (InputStream inputStream = new ClassPathResource("./player.json").getInputStream()) {
            System.out.println("Got the data --------------------------------------->>>>>>>>>");
            List<Character> charactersFromJson = objectMapper.readValue(inputStream, new TypeReference<List<Character>>() {});

            for (Character character : charactersFromJson) {
                // 1. Process and save Effects first (due to ManyToMany with Ability)
                List<Effect> persistentEffects = new ArrayList<>();
                if (character.getAbility() != null && character.getAbility().getEffects() != null) {
                    for (Effect effect : character.getAbility().getEffects()) {
                        // Check if an effect with the same name already exists to prevent duplicates for common effects
                        Effect existingEffect = effectRepository.findByName(effect.getName());
                        // Effect existingEffect = effectRepository.findById(effect.getName());
                        if (existingEffect != null) {
                            persistentEffects.add(existingEffect);
                        } else {
                            // Important: Clear the transient 'id' and 'turns' fields from JSON for persistence
                            // The database will generate new IDs. 'turns' is @Transient anyway.
                            effect.setId(null); // Let DB generate ID
                            effect.setTurns(null); // @Transient field, won't be saved, but good to null out

                            Effect savedEffect = effectRepository.save(effect);
                            persistentEffects.add(savedEffect);
                        }
                    }
                    character.getAbility().setEffects(persistentEffects);
                }

                // 2. Process and save Ability (OneToOne with Character)
                if (character.getAbility() != null) {
                    // Important: Clear the transient 'id' from JSON for persistence
                    // The database will generate new IDs for abilities.
                    character.getAbility().setId(null); // Let DB generate ID

                    Ability savedAbility = abilityRepository.save(character.getAbility());
                    character.setAbility(savedAbility); // Set the managed Ability object back to the Character
                }

                // 3. Save Character
                // Important: Clear the transient 'id' from JSON for persistence
                // The database will generate new IDs for characters.
                character.setId(null); // Let DB generate ID
                character.setCurrentHealth(character.getBaseHealth()); // Initialize current health
                characterRepository.save(character);
                logger.info("Saved character: {}", character.getName());
            }
            logger.info("Successfully loaded {} characters into the database.", characterRepository.count());

        } catch (IOException e) {
            logger.error("Failed to load character data from JSON file: {}", e.getMessage(), e);
            throw e; // Re-throw to indicate a critical failure
        }
    }
}
