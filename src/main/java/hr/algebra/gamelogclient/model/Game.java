package hr.algebra.gamelogclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private Long id;
    private String title;
    private String developer;
    private String publisher;
    private Integer releaseYear;

    private String genre;
    private String secondaryGenre;
    private String platform;
    private String status;
    private String difficulty;
    private String playStyle;

    private Integer hoursPlayed;
    private Integer completionPercent;
    private Integer achievementsUnlocked;
    private Integer achievementsTotal;
    private Integer rating;
    private Integer storyScore;
    private Integer gameplayScore;
    private Integer visualsScore;
    private Integer soundtrackScore;
    private Integer replayValue;

    private boolean platinumTrophy;
    private boolean gotyContender;
    private boolean comfortGame;
    private boolean recommendToFriend;

    private LocalDate purchaseDate;
    private LocalDate startedDate;
    private LocalDate finishedDate;

    private BigDecimal pricePaidEur;

    private String currentBuild;
    private String moodTags;
    private String mainProtagonist;
    private String favoriteMoment;
    private String review;
    private String personalNotes;

    public Game() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getSecondaryGenre() { return secondaryGenre; }
    public void setSecondaryGenre(String secondaryGenre) { this.secondaryGenre = secondaryGenre; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getPlayStyle() { return playStyle; }
    public void setPlayStyle(String playStyle) { this.playStyle = playStyle; }

    public Integer getHoursPlayed() { return hoursPlayed; }
    public void setHoursPlayed(Integer hoursPlayed) { this.hoursPlayed = hoursPlayed; }

    public Integer getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(Integer completionPercent) { this.completionPercent = completionPercent; }

    public Integer getAchievementsUnlocked() { return achievementsUnlocked; }
    public void setAchievementsUnlocked(Integer achievementsUnlocked) { this.achievementsUnlocked = achievementsUnlocked; }

    public Integer getAchievementsTotal() { return achievementsTotal; }
    public void setAchievementsTotal(Integer achievementsTotal) { this.achievementsTotal = achievementsTotal; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getStoryScore() { return storyScore; }
    public void setStoryScore(Integer storyScore) { this.storyScore = storyScore; }

    public Integer getGameplayScore() { return gameplayScore; }
    public void setGameplayScore(Integer gameplayScore) { this.gameplayScore = gameplayScore; }

    public Integer getVisualsScore() { return visualsScore; }
    public void setVisualsScore(Integer visualsScore) { this.visualsScore = visualsScore; }

    public Integer getSoundtrackScore() { return soundtrackScore; }
    public void setSoundtrackScore(Integer soundtrackScore) { this.soundtrackScore = soundtrackScore; }

    public Integer getReplayValue() { return replayValue; }
    public void setReplayValue(Integer replayValue) { this.replayValue = replayValue; }

    public boolean isPlatinumTrophy() { return platinumTrophy; }
    public void setPlatinumTrophy(boolean platinumTrophy) { this.platinumTrophy = platinumTrophy; }

    public boolean isGotyContender() { return gotyContender; }
    public void setGotyContender(boolean gotyContender) { this.gotyContender = gotyContender; }

    public boolean isComfortGame() { return comfortGame; }
    public void setComfortGame(boolean comfortGame) { this.comfortGame = comfortGame; }

    public boolean isRecommendToFriend() { return recommendToFriend; }
    public void setRecommendToFriend(boolean recommendToFriend) { this.recommendToFriend = recommendToFriend; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getStartedDate() { return startedDate; }
    public void setStartedDate(LocalDate startedDate) { this.startedDate = startedDate; }

    public LocalDate getFinishedDate() { return finishedDate; }
    public void setFinishedDate(LocalDate finishedDate) { this.finishedDate = finishedDate; }

    public BigDecimal getPricePaidEur() { return pricePaidEur; }
    public void setPricePaidEur(BigDecimal pricePaidEur) { this.pricePaidEur = pricePaidEur; }

    public String getCurrentBuild() { return currentBuild; }
    public void setCurrentBuild(String currentBuild) { this.currentBuild = currentBuild; }

    public String getMoodTags() { return moodTags; }
    public void setMoodTags(String moodTags) { this.moodTags = moodTags; }

    public String getMainProtagonist() { return mainProtagonist; }
    public void setMainProtagonist(String mainProtagonist) { this.mainProtagonist = mainProtagonist; }

    public String getFavoriteMoment() { return favoriteMoment; }
    public void setFavoriteMoment(String favoriteMoment) { this.favoriteMoment = favoriteMoment; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public String getPersonalNotes() { return personalNotes; }
    public void setPersonalNotes(String personalNotes) { this.personalNotes = personalNotes; }
}