package be.howest.photoweave.model.binding;

import be.howest.photoweave.model.util.ConfigUtil;
import be.howest.photoweave.model.util.ImageUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class BindingFactory {
    private static BindingFactory instance = null;

    private List<Binding> bindings = new ArrayList<>();
    private final Integer MAX_INTERNAL_BINDINGS = 24;
    private Binding[] optimizedBindings;
    private HashMap<String, List<Binding>> allBindings = new HashMap<>();

    protected BindingFactory() {
        try {
            getBindingsFromInternalResources();
        } catch (Exception e) {
            System.out.println("Internal Resources could not be found || e: " + e.getMessage());
        }
    }

    public static BindingFactory getInstance() {
        if (instance == null) instance = new BindingFactory();

        return instance;
    }

    private void getBindingsFromInternalResources() throws Exception {
        File[] directories = new File(ConfigUtil.getBindingsPath()).listFiles(File::isDirectory);

        for (File directory : directories){
            Collection<File> files = FileUtils.listFiles(
                    directory, new String[] {"bmp"}, true);

            List<Binding> localBindings = new ArrayList<>();
            for (File file : files) {
                InputStream is = new FileInputStream(file);
                Binding b = new Binding(is,file.getName());
                localBindings.add(b);
                this.bindings.add(b);
            }
            allBindings.put(directory.getName(),localBindings);
        }

        optimizedBindings = getSortedBindings(allBindings.get("default")).toArray(new Binding[allBindings.get("default").size()]);
    }

    public void updateNewBindings() throws Exception {
        File[] directories = new File(ConfigUtil.getBindingsPath()).listFiles(File::isDirectory);

        for (File directory : directories){
            Collection<File> files = FileUtils.listFiles(
                    directory, new String[] {"bmp"}, true);

            boolean directoryAlreadyExists = allBindings.containsKey(directory.getName());

            List<Binding> localBindings = directoryAlreadyExists ? allBindings.get(directory.getName()) : new ArrayList<>();
            for (File file : files) {
                InputStream is = new FileInputStream(file);
                Binding b = new Binding(is,file.getName());

                if (directoryAlreadyExists) {
                    boolean isNewBinding = true;
                    for (Binding binding : localBindings) {
                        if (binding.getName().equals(b.getName())) {
                            isNewBinding = false;
                        }
                    }

                    if (isNewBinding) {
                        localBindings.add(b);
                        this.bindings.add(b);
                    }
                } else {
                    localBindings.add(b);
                    this.bindings.add(b);
                }
            }

            if (!directoryAlreadyExists) allBindings.put(directory.getName(),localBindings);
        }
    }

    public List<Binding> getBindings() {
        return bindings;
    }


    public Binding[] getOptimizedBindings() {
        return optimizedBindings;
    }

    public List<Binding> getSortedBindings(List<Binding> bindingList) {
        HashMap<Binding, Integer> bindingIntensityMap = new HashMap<>();

        for (int j = 0; j < bindingList.size(); j++) {
            convertToRBGIntImages(bindingList.get(j));
            setIntensityFromBindings(bindingIntensityMap, bindingList.get(j));
        }

        return new ArrayList<>(getSortedIntensity(bindingIntensityMap));
    }

    //TODO change?
    private Set<Binding> getSortedIntensity(HashMap<Binding, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)).keySet();
    }

    private void convertToRBGIntImages(Binding binding) {
        binding.setBindingImage(ImageUtil.convertImageToRGBInt(binding.getBindingImage()));
    }
    private void setIntensityFromBindings(HashMap map, Binding binding) {
        map.put(binding, binding.getIntensityCount());
    }

    public HashMap<String, List<Binding>> getAllBindings() {
        return allBindings;
    }
}