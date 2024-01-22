package fr.uge.gitclout.database;

import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.*;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * This class is used to manage the database .
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Singleton
public class DataBaseService {
  private final ContributionRepository contributionRepository;
  private final RepositoryRepo repositoryRepo;
  private final ContributorRepository contributorRepository;
  private final TagRepository tagRepository;

  @Inject
  DataBaseService(ContributionRepository contributionRepository, RepositoryRepo repositoryRepo,
                         ContributorRepository contributorRepository, TagRepository tagRepository) {
    this.contributionRepository = contributionRepository;
    this.repositoryRepo = repositoryRepo;
    this.contributorRepository = contributorRepository;
    this.tagRepository = tagRepository;
  }


  public void delete(Repository repository) {
    repositoryRepo.delete(repository);
  }
  
  
  @io.micronaut.data.annotation.Repository
  interface ContributionRepository extends CrudRepository<Contribution, Long> {
    List<Contribution> findByTag(Tag tag);
  }


  @io.micronaut.data.annotation.Repository
  interface RepositoryRepo extends CrudRepository<Repository, Long> {
    Optional<Repository> findByUrl(String url);
  }

  
   @io.micronaut.data.annotation.Repository
  interface ContributorRepository extends CrudRepository<Contributor, Long> {
    Contributor findByUsername(String name);
    boolean existsByUsername(String contributorName);
  }

  
  @io.micronaut.data.annotation.Repository
  interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findByName(String name);
    boolean existsByName(String tag);
    Tag findBySha1(String sha1);
  }

  
  public void save(Tag tag) {
    Objects.requireNonNull(tag);
    tagRepository.save(tag);
  }


  public void save(Repository repository) {
    Objects.requireNonNull(repository);
    repositoryRepo.save(repository);
  }


  public Contributor save(Contributor contributor) {
    Objects.requireNonNull(contributor);
    return contributorRepository.save(contributor);
  }


  public void save(Contribution contribution) {
    Objects.requireNonNull(contribution);
    contributionRepository.save(contribution);
  }

  

  
  public List<Tag> getTag() {
    return tagRepository.findAll();
  }

  
  public Optional<Repository> getRepositoryByUrl(String url) {
    return repositoryRepo.findByUrl(url);
  }

  
  public @NonNull Optional<Repository> getRepositoryById(long id) {
    return repositoryRepo.findById(id);
  }


  public List<Repository> findAll() {
    return repositoryRepo.findAll();
  }

  
  public @NonNull Contributor findContributorByUsername(String contributorName) {
    return contributorRepository.findByUsername(contributorName);
  }


  public Optional<List<Contribution>> getContributionPerTag(Tag tag) {
    Objects.requireNonNull(tag);
    return Optional.ofNullable(contributionRepository.findByTag(tag));
  }


  public Tag findTagByName(String name) {
    return tagRepository.findByName(name);
  }

  
  public void delete(Tag tag){
    Objects.requireNonNull(tag);
    tagRepository.delete(tag);
  }


  public void delete(Contributor contributor){
    contributorRepository.delete(contributor);
  }

  
  public boolean existsByUsername(String contributorName){
    return contributorRepository.existsByUsername(contributorName);
  }


  public boolean existsByNameTag(String tag){
    return tagRepository.existsByName(tag);
  }


  public Tag findBySha1(String sha1){
    return tagRepository.findBySha1(sha1);
  }
}