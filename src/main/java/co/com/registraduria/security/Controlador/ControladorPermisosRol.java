package co.com.registraduria.security.Controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.com.registraduria.security.Modelo.Permiso;
import co.com.registraduria.security.Modelo.Rol;
import co.com.registraduria.security.Modelo.PermisosRol;
import co.com.registraduria.security.Repositorio.RepositorioPermiso;
import co.com.registraduria.security.Repositorio.RepositorioPermisosRol;
import co.com.registraduria.security.Repositorio.RepositorioRol;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/permisos-roles")
public class ControladorPermisosRol{
    @Autowired
    private RepositorioPermisosRol miRepositorioPermisosRol;

    @Autowired
    private RepositorioPermiso miRepositorioPermiso;

    @Autowired
    private RepositorioRol miRepositorioRol;


    @GetMapping("")
    public List<PermisosRol> index(){
        return this.miRepositorioPermisosRol.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> show (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        PermisosRol permisosRol=this.miRepositorioPermisosRol.findById(id).orElse(null);
        if(permisosRol !=null){
            response.put("Response",permisosRol);
            return ResponseEntity.ok(response);
        }
        response.put("Error","El permiso-rol no exite");
        return ResponseEntity.badRequest().body(response);
    }
    /**
    @GetMapping("{id}")
    public PermisosRol show(@PathVariable String id){
        PermisosRol permisosRolesActual=this.miRepositorioPermisosRol.findById(id).orElse(null);
        return permisosRolesActual;
    }**/

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("rol/{id_rol}/permiso/{id_permiso}")
    public ResponseEntity<HashMap<String,Object>> create(@PathVariable String id_rol,@PathVariable String id_permiso){
        HashMap<String,Object> response = new HashMap<>();
        PermisosRol nuevo=new PermisosRol();
        var rol=this.miRepositorioRol.findById(id_rol);
        var permiso=this.miRepositorioPermiso.findById(id_permiso);
        if (rol.isEmpty()){
            response.put("Error","El rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        if (permiso.isEmpty()){
            response.put("Error","El permiso no existe");
            return ResponseEntity.badRequest().body(response);
        }
        nuevo.setPermiso(permiso.get());
        nuevo.setRol(rol.get());
        response.put("Response",this.miRepositorioPermisosRol.save(nuevo));
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}/rol/{id_rol}/permiso/{id_permiso}")
    public ResponseEntity<HashMap<String,Object>> update(@PathVariable String id,@PathVariable String id_rol,@PathVariable String id_permiso){
        HashMap<String,Object> response =new HashMap<>();
        PermisosRol permisosRolesActual=this.miRepositorioPermisosRol.findById(id).orElse(null);
        if (permisosRolesActual==null){
            response.put("Error","El permiso-rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        var rol=this.miRepositorioRol.findById(id_rol);
        if (rol.isEmpty()){
            response.put("Error","El rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        var permiso=this.miRepositorioPermiso.findById(id_permiso);
        if (permiso.isEmpty()){
            response.put("Error","El permiso no existe");
            return ResponseEntity.badRequest().body(response);
        }
        permisosRolesActual.setPermiso(permiso.get());
        permisosRolesActual.setRol(rol.get());
        response.put("Response",this.miRepositorioPermisosRol.save(permisosRolesActual));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HashMap<String,Object>> delete(@PathVariable String id){
        HashMap<String,Object> response = new HashMap<>();
        var permisosRolesActual=this.miRepositorioPermisosRol.findById(id);
        if (permisosRolesActual.isEmpty()){
            response.put("Error","El permiso-rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        this.miRepositorioPermisosRol.delete(permisosRolesActual.get());
        response.put("Response","Permiso-rol eliminado");
        return ResponseEntity.ok(response);
    }

    @GetMapping("validar-permiso/rol/{id_rol}")
    public PermisosRol getPermiso(@PathVariable String id_rol, @RequestBody Permiso infoPermiso){
        Permiso elPermiso=this.miRepositorioPermiso.getPermiso(infoPermiso.getUrl(),infoPermiso.getMetodo());
        Rol elRol=this.miRepositorioRol.findById(id_rol).get();
        if (elPermiso!=null && elRol!=null){
            return this.miRepositorioPermisosRol.getPermisoRol(elRol.get_id(),elPermiso.get_id());
        }else{
            return null;
        }
    }
}
