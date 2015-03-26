import org.junit.*;

import java.util.*;
import play.test.*;
import validadores.RenavamCheck;
import models.*;

public class UpperCaseTest extends UnitTest {
	UsuarioTeste user = new UsuarioTeste();
	String nome = "baylon".toUpperCase();
	String endereco = "rua.voluntarios da patria".toUpperCase();
	String bairro = "botafogo".toUpperCase();
	
	
	@Test
	public void testaUpperCase() {
		user.nome = "baylon";
		user.endereco = "rua.voluntarios da patria";
		user.bairro = "botafogo";
		user.numero = 11111;
		user.save();
		Assert.assertEquals(user.nome, nome);
		Assert.assertEquals(user.endereco, endereco);
		Assert.assertNotSame(user.bairro, bairro);
		
		
    }
 
}