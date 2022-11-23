package co.com.registraduria.security.Controlador;
import co.com.registraduria.security.Modelo.Usuario;
import co.com.registraduria.security.Modelo.Rol;
import co.com.registraduria.security.Repositorio.RepositorioRol;
import co.com.registraduria.security.Repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class ControladorUsuario {
    @Autowired
    private RepositorioUsuario miRepositorioUsuario;
    @Autowired
    private RepositorioRol miRepositorioRol;
    @GetMapping("")
    public List<Usuario> index(){
        return this.miRepositorioUsuario.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<HashMap<String,Object>> show(@PathVariable String id){
        HashMap<String,Object> response= new HashMap<>();
        Usuario usuarioActual=this.miRepositorioUsuario.findById(id).orElse(null);
        if(usuarioActual !=null){
            response.put("Response",usuarioActual);
            return ResponseEntity.ok(response);
        }
        response.put("Error","El usuario no exite");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/rol/{id_rol}")
    public ResponseEntity<HashMap<String,Object>> create(@PathVariable String id_rol,@RequestBody  Usuario infoUsuario){
        HashMap<String,Object> response = new HashMap<>();
        if (Objects.isNull(infoUsuario.getSeudonimo()) || infoUsuario.getSeudonimo().isEmpty()){
            response.put("Error","Debe ingresar un seudonimo");
            return ResponseEntity.badRequest().body(response);
        }
        if (Objects.isNull(infoUsuario.getCorreo()) || infoUsuario.getCorreo().isEmpty()){
            response.put("Error","Debe ingresar un correo");
            return ResponseEntity.badRequest().body(response);
        }
        var validarCorreo = this.miRepositorioUsuario.getUserByEmail(infoUsuario.getCorreo());
        if (!Objects.isNull(validarCorreo)){
            response.put("Response","Ingrese otro correo");
            return ResponseEntity.ok(response);
        }
        if (Objects.isNull(infoUsuario.getContrasena()) || infoUsuario.getContrasena().isEmpty()){
            response.put("Error","Debe ingresar una contraseña");
            return ResponseEntity.badRequest().body(response);
        }
        infoUsuario.setContrasena(convertirSHA256(infoUsuario.getContrasena()));
        Rol rolActual=this.miRepositorioRol.findById(id_rol).orElse(null);
        if(rolActual==null){
            response.put("Error","El rol no existe");
            return ResponseEntity.badRequest().body(response);
        }
        infoUsuario.setRol(rolActual);
        response.put("Response",this.miRepositorioUsuario.save(infoUsuario));
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> update(@PathVariable String id, @RequestBody Usuario infoUsuario){
        HashMap<String, Object> response = new HashMap<>();
        if(Objects.isNull(infoUsuario.getSeudonimo()) || infoUsuario.getSeudonimo().isEmpty()){
            response.put("Error", "Debe ingresar un seudonimo");
            return ResponseEntity.badRequest().body(response);
        }
        if (Objects.isNull(infoUsuario.getContrasena()) || infoUsuario.getContrasena().isEmpty()){
            response.put("Error", "Debe ingresar una contraseña");
            return ResponseEntity.badRequest().body(response);
        }
        var usuarioActual=this.miRepositorioUsuario.findById(id).orElse(null);
        if (usuarioActual!=null){
            usuarioActual.setSeudonimo(infoUsuario.getSeudonimo());
            usuarioActual.setContrasena(infoUsuario.getContrasena());
            response.put("Response",this.miRepositorioUsuario.save(usuarioActual));
            return ResponseEntity.ok(response);
        }
        response.put("Error","El usuario no existe");
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> delete (@PathVariable String id){
        HashMap<String, Object> response = new HashMap<>();
        Usuario usuarioActual = this.miRepositorioUsuario.findById(id).orElse(null);
        if(usuarioActual !=null){
            this.miRepositorioUsuario.delete(usuarioActual);
            response.put("Response","Usuario eliminado");
            return ResponseEntity.ok(response);
        }
        response.put("Error","El usuario no existe");
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("{id}/rol/{id_rol}")
    public Usuario asignarRolAUsuario(@PathVariable String id,@PathVariable String id_rol){
        Usuario usuarioActual=this.miRepositorioUsuario.findById(id).orElse(null);
        if(usuarioActual==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El usuario solicitado no existe");
        }
        Rol rolActual=this.miRepositorioRol.findById(id_rol).orElse(null);
        if(rolActual==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El rol solicitado no existe");
        }
        usuarioActual.setRol(rolActual);
        return this.miRepositorioUsuario.save(usuarioActual);
    }

    public String convertirSHA256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @PostMapping("/validar")
    public Usuario validate(@RequestBody  Usuario infoUsuario,
                            final HttpServletResponse response) throws IOException {
        Usuario usuarioActual=this.miRepositorioUsuario.getUserByEmail(infoUsuario.getCorreo());
        if (usuarioActual!=null && usuarioActual.getContrasena().equals(convertirSHA256(infoUsuario.getContrasena()))) {
            usuarioActual.setContrasena("");
            return usuarioActual;
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }
}
