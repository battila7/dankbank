package net.daergoth.dankbank.meme;

import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemeDaoImpl implements MemeDao {

    private final TagDao tagDao;

    private final File saveDirectory;

    private List<Meme> cachedMemes;

    public MemeDaoImpl(TagDao tagDao, File saveDirectory) {
        this.tagDao = tagDao;

        this.saveDirectory = saveDirectory;

        this.cachedMemes = new ArrayList<>();
    }

    @Override
    public List<Meme> getAllMemes() {
        return Collections.unmodifiableList(cachedMemes);
    }

    @Override
    public void addMeme(Meme m) {
        if (m == null) {
            throw new NullPointerException("Meme cannot be null!");
        }

        if (!cachedMemes.contains(m)) {
            cachedMemes.add(m);

            saveChanges();
        }
    }

    @Override
    public List<Meme> getMemesByTag(final Tag t) {
        List<Meme> memesWithGivenTag = new ArrayList<>();

        for (Meme m : cachedMemes) {
            if(m.getTags().contains(t)) {
                memesWithGivenTag.add(m);
            }
        }

        return memesWithGivenTag;
    }

    private void saveChanges() {

    }
}