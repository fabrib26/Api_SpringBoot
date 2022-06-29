package com.sistemablogspringboot.servicio.impl;

import com.sistemablogspringboot.dto.PublicacionDTO;
import com.sistemablogspringboot.dto.PublicacionRespuesta;
import com.sistemablogspringboot.entidades.Publicacion;
import com.sistemablogspringboot.excepciones.ResourceNotFoundException;
import com.sistemablogspringboot.repositorio.PublicacionRepositorio;
import com.sistemablogspringboot.servicio.PublicacionServicio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServicioImpl implements PublicacionServicio {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PublicacionRepositorio publicacionRepositorio;

    @Override
    public PublicacionDTO crearPublicacion(PublicacionDTO publicacionDTO) {
        Publicacion publicacion = mapearEntidad(publicacionDTO);

        Publicacion nuevaPublicacion = publicacionRepositorio.save(publicacion);

        PublicacionDTO publicacionRespuesta = mapearDTO(nuevaPublicacion);
        return publicacionRespuesta;
    }

    @Override
    public PublicacionRespuesta obtenerTodasLasPublicaciones(int numeroDePagina, int medidaDePagina, String ordenarPor,
                                                             String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();
        Pageable pageable = PageRequest.of(numeroDePagina, medidaDePagina, sort);

        Page<Publicacion> publicaciones = publicacionRepositorio.findAll(pageable);

        List<Publicacion> listaDePublicaciones = publicaciones.getContent();
        List<PublicacionDTO> contenido = listaDePublicaciones.stream().map(publicacion -> mapearDTO(publicacion))
                .collect(Collectors.toList());

        PublicacionRespuesta publicacionRespuesta = new PublicacionRespuesta();
        publicacionRespuesta.setContenido(contenido);
        publicacionRespuesta.setNumeroDePagina(publicaciones.getNumber());
        publicacionRespuesta.setMedidaDePagina(publicaciones.getSize());
        publicacionRespuesta.setTotalDeElementos(publicaciones.getTotalElements());
        publicacionRespuesta.setTotalDePaginas(publicaciones.getTotalPages());
        publicacionRespuesta.setUltima(publicaciones.isLast());

        return publicacionRespuesta;
    }

    @Override
    public PublicacionDTO obtenerPublicacionPorId(long id) {
        Publicacion publicacion = publicacionRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", id));
        return mapearDTO(publicacion);
    }

    @Override
    public PublicacionDTO actualizarPublicacion(PublicacionDTO publicacionDTO, long id) {
        Publicacion publicacion = publicacionRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", id));

        publicacion.setTitulo(publicacionDTO.getTitulo());
        publicacion.setDescripcion(publicacionDTO.getDescripcion());
        publicacion.setContenido(publicacionDTO.getContenido());

        Publicacion publicacionActualizada = publicacionRepositorio.save(publicacion);
        return mapearDTO(publicacionActualizada);
    }

    @Override
    public void eliminarPublicacion(long id) {
        Publicacion publicacion = publicacionRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", "id", id));
        publicacionRepositorio.delete(publicacion);
    }

    // Convierte entidad a DTO
    private PublicacionDTO mapearDTO(Publicacion publicacion) {
        PublicacionDTO publicacionDTO = modelMapper.map(publicacion, PublicacionDTO.class);
        return publicacionDTO;
    }

    // Convierte de DTO a Entidad
    private Publicacion mapearEntidad(PublicacionDTO publicacionDTO) {
        Publicacion publicacion = modelMapper.map(publicacionDTO, Publicacion.class);
        return publicacion;
    }
}