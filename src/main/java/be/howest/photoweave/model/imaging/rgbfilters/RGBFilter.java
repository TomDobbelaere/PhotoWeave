package be.howest.photoweave.model.imaging.rgbfilters;

import be.howest.photoweave.model.imaging.Filter;

/**
 * RGBFilters access one single rgb color at a time, returning a possibly modified rgb color to come in place.
 * These filters are fast and can be multithreaded easily, but cannot keep track of state and therefore have less possibilities.
 */
public interface RGBFilter extends Filter {
    int applyTo(int rgb, int i, byte[] imageMetaData);
}
