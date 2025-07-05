package it.mulders.traqqr.domain.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks the 'default' constructor to MapStruct.
 * <p>
 * {@see <a href="https://github.com/mapstruct/mapstruct/blob/main/documentation/src/main/asciidoc/chapter-3-defining-a-mapper.asciidoc#using-constructors">Using Constructors</a>} in the MapStruct documentation.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(value = RetentionPolicy.SOURCE)
public @interface Default {}
