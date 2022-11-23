package co.com.registraduria.security.Controlador;

import co.com.registraduria.security.Repositorio.RepositorioPermisosRol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.com.registraduria.security.Modelo.Permiso;
import co.com.registraduria.security.Repositorio.RepositorioPermiso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/permisos")
public class ControladorPermiso {
    @Autowired
    private RepositorioPermiso repositorioPermiso;

    @Autowired
    private RepositorioPermisosRol repositorioPermisosRol;

    @GetMapping("")
    public List<Permiso> index(){
        return this.repositorioPermiso.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> show (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        Permiso permisoActual=this.repositorioPermiso.findById(id).orElse(null);
        if(permisoActual !=null){
            response.put("Response",permisoActual);
            return ResponseEntity.ok(response);
        }
        response.put("Error","El rol no exite");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping
    public ResponseEntity<HashMap<String, Object>> create(@RequestBody Permiso info){
        HashMap<String, Object> response = new HashMap<>();
        if(Objects.isNull(info.getUrl()) || info.getUrl().isEmpty()){
            response.put("Error","Debe ingresar una url");
            return ResponseEntity.badRequest().body(response);
        }
        if (Objects.isNull(info.getMetodo()) || info.getMetodo().isEmpty()){
            response.put("Error","Debe ingresar un metodo");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("Response", this.repositorioPermiso.save(info));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> delete (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        var permisoActual = this.repositorioPermiso.findById(id);
        if(permisoActual.isEmpty()){
            response.put("Error","El permiso no existe");
            return ResponseEntity.badRequest().body(response);
        }
        var lista = this.repositorioPermisosRol.findAll()
                .stream()
                .filter(permisosRol -> permisosRol.getPermiso().get_id().equals(id))
                .collect(Collectors.toList());
        if (!lista.isEmpty()){
            response.put("Error","Existen roles con ese permiso");
            return ResponseEntity.badRequest().body(response);
        }
        this.repositorioPermiso.delete(permisoActual.get());
        response.put("Response","Permiso eliminado");
        return ResponseEntity.ok(response);
    }
}
