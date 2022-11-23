package co.com.registraduria.security.Controlador;

import co.com.registraduria.security.Repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.com.registraduria.security.Modelo.Rol;
import co.com.registraduria.security.Repositorio.RepositorioRol;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/roles")
public class ControladorRol {
    @Autowired
    private RepositorioRol repositorioRol;
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    @GetMapping("")
    public List<Rol> index(){
        return this.repositorioRol.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> show (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        Rol rolActual=this.repositorioRol.findById(id).orElse(null);
        if(rolActual !=null){
            response.put("Response",rolActual);
            return ResponseEntity.ok(response);
        }
        response.put("Error","El rol no exite");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping
    public ResponseEntity<HashMap<String, Object>> create(@RequestBody Rol infoRol){
        HashMap<String, Object> response = new HashMap<>();
        if(Objects.isNull(infoRol.getDescripcion()) || infoRol.getDescripcion().isEmpty()){
            response.put("Error","Debe ingresar una descripción");
            return ResponseEntity.badRequest().body(response);
        }
        if (Objects.isNull(infoRol.getNombre()) || infoRol.getNombre().isEmpty()){
            response.put("Error","Debe ingresar un nombre");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("Response", this.repositorioRol.save(infoRol));
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> update(@PathVariable String id, @RequestBody Rol infoRol){
        HashMap<String, Object> response = new HashMap<>();
        if(Objects.isNull(infoRol.getNombre()) || infoRol.getNombre().isEmpty()){
            response.put("Error", "Debe ingresar un nombre");
            return ResponseEntity.badRequest().body(response);
        }
        if (Objects.isNull(infoRol.getDescripcion()) || infoRol.getDescripcion().isEmpty()){
            response.put("Error", "Debe ingresar una descripcion");
            return ResponseEntity.badRequest().body(response);
        }
        Rol rolActual=this.repositorioRol.findById(id).orElse(null);
        if (rolActual!=null){
            rolActual.setNombre(infoRol.getNombre());
            rolActual.setDescripcion(infoRol.getDescripcion());
            response.put("Response",this.repositorioRol.save(rolActual));
            return ResponseEntity.ok(response);
        }
        response.put("Error","El rol no existe");
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> delete (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        var rolActual = this.repositorioRol.findById(id);
        if(rolActual.isEmpty()){
            response.put("Error","El rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        var lista = this.repositorioUsuario.findAll()
                .stream()
                .filter(usuario -> usuario.getRol().get_id().equals(id))
                .collect(Collectors.toList());
        if(!lista.isEmpty()){
            response.put("Error","Existen usuarios con ese rol");
            return ResponseEntity.badRequest().body(response);
        }
        this.repositorioRol.delete(rolActual.get());
        response.put("Response","Rol eliminado");
        return ResponseEntity.ok(response);
    }
}
