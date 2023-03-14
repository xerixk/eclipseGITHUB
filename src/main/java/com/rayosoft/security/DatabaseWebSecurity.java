package com.rayosoft.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity{
	
	/*@Bean
	UserDetailsManager users(DataSource dataSource ) {
	JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
	return users;
	}
	*/
	@Bean
	public UserDetailsManager usersCustom(DataSource dataSource) {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
		users.setUsersByUsernameQuery("select username, password, estatus from Usuarios where username=?");
			users.setAuthoritiesByUsernameQuery("select u.username, p.perfil from UsuarioPerfil up " + 
												"inner join Usuarios u on u.id = up.idUsuario "	+ 
												"inner join Perfiles p on p.id = up.idPerfil " + "where u.username = ?");
		return users;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()

		// Los recursos estáticos no requieren autenticación
		.requestMatchers("/bootstrap/**", "/images/**", "/tinymce/**", "/logos/**").permitAll()

		// Las vistas públicas no requieren autenticación
		.requestMatchers("/","/login","/signup", "/search","/bcrypt/**","/about","/vacantes/view/**").permitAll()
		
		.requestMatchers("/solicitudes/create/**","/solicitudes/save/**").hasAnyAuthority("USUARIO")
		
		.requestMatchers("/solicitudes/**").hasAnyAuthority("SUPERVISOR","ADMINISTRADOR")
		.requestMatchers("/vacantes/**").hasAnyAuthority("SUPERVISOR","ADMINISTRADOR")
		.requestMatchers("/categorias/**").hasAnyAuthority("SUPERVISOR","ADMINISTRADOR")
		.requestMatchers("/usuarios/**").hasAnyAuthority("ADMINISTRADOR")
		
		
		// Todas las demás URLs de la Aplicación requieren autenticación
		.anyRequest().authenticated()
		
		// El formulario de Login no requiere autenticacion
		.and().formLogin().loginPage("/login").permitAll()
		.and().logout().permitAll();

		return http.build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncode() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	
	
	
}