package it.mulders.traqqr.web.krazo;

import jakarta.servlet.annotation.HandlesTypes;
import jakarta.ws.rs.Path;
import org.eclipse.krazo.servlet.KrazoContainerInitializer;

@HandlesTypes({Path.class})
public class TraqqrServletContainerInitializer extends KrazoContainerInitializer {}
