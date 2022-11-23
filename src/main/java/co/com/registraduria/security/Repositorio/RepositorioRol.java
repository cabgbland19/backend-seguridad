package co.com.registraduria.security.Repositorio;
import co.com.registraduria.security.Modelo.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioRol extends MongoRepository<Rol,String>{
}
