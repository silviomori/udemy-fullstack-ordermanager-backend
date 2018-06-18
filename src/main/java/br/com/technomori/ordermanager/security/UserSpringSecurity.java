package br.com.technomori.ordermanager.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.technomori.ordermanager.domain.enums.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder

public class UserSpringSecurity implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Getter
	private Integer id;
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setUserProfiles(Set<UserProfile> userProfiles) {
		authorities = userProfiles.stream().map( profile -> new SimpleGrantedAuthority(profile.getDescription()) )
				.collect(Collectors.toList());
	}

	public boolean hasRole(UserProfile profile) {
		return authorities.contains(new SimpleGrantedAuthority(profile.getDescription()));
	}

}
