package com.rpg_game.game.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.entity.Ability;
import com.rpg_game.game.entity.Effect;
import com.rpg_game.game.repositories.CharacterRepository;
import com.rpg_game.game.repositories.AbilityRepository; // Import new repositories
import com.rpg_game.game.repositories.EffectRepository;   // Import new repositories
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CharacterRepository characterRepository;
    private final AbilityRepository abilityRepository;
    private final EffectRepository effectRepository;
    private final ObjectMapper objectMapper;

    // Inject all necessary repositories
    public DataLoader(CharacterRepository characterRepository, 
                           AbilityRepository abilityRepository, 
                           EffectRepository effectRepository, 
                           ObjectMapper objectMapper) {
        this.characterRepository = characterRepository;
        this.abilityRepository = abilityRepository;
        this.effectRepository = effectRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (characterRepository.count() > 0) {
            System.out.println("--- Character Data Already Exists, Skipping Seed ---");
            return;
        }
        
        System.out.println("--- Seeding Character Data ---");

        try (InputStream inputStream = new ClassPathResource("player.json").getInputStream()) {
            List<Character> charactersFromJson = objectMapper.readValue(inputStream, new TypeReference<>() {});

            for (Character character : charactersFromJson) {
                
                // --- THE FIX: Manually handle the nested objects ---

                Ability ability = character.getAbility();
                if (ability != null) {
                    
                    List<Effect> effects = new ArrayList<>(ability.getEffects()); // Create a mutable copy
                    ability.setEffects(new ArrayList<>()); // Clear the original list on the ability

                    // 1. Save the Effects first
                    List<Effect> savedEffects = new ArrayList<>();
                    for (Effect effect : effects) {
                        // IMPORTANT: Set ID to null to force an INSERT and let DB generate the ID
                        effect.setId(null); 
                        savedEffects.add(effectRepository.save(effect));
                    }

                    // 2. Save the Ability
                    // Set ID to null to force an INSERT
                    ability.setId(null);
                    // Add the now-persistent effects back to the ability
                    ability.setEffects(savedEffects);
                    abilityRepository.save(ability);
                }

                // 3. Finally, save the Character
                // Set ID to null to force an INSERT
                character.setId(null); 
                character.setAbility(ability); // Ensure the managed ability is set
                characterRepository.save(character);
            }
            
            System.out.println("--- " + charactersFromJson.size() + " Characters Seeded Successfully ---");
        } catch (Exception e) {
            System.err.println("Error seeding character data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}