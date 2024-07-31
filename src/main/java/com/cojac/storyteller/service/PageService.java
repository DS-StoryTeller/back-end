package com.cojac.storyteller.service;

import com.cojac.storyteller.code.ErrorCode;
import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.PageEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.UnknownWordEntity;
import com.cojac.storyteller.dto.page.PageDetailResponseDTO;
import com.cojac.storyteller.dto.request.PageRequestDTO;
import com.cojac.storyteller.dto.unknownWord.UnknownWordDTO;
import com.cojac.storyteller.exception.BookNotFoundException;
import com.cojac.storyteller.exception.PageNotFoundException;
import com.cojac.storyteller.exception.ProfileNotFoundException;
import com.cojac.storyteller.exception.UnknownWordNotFoundException;
import com.cojac.storyteller.repository.BookRepository;
import com.cojac.storyteller.repository.PageRepository;
import com.cojac.storyteller.repository.ProfileRepository;
import com.cojac.storyteller.repository.UnknownWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final UnknownWordRepository unknownWordRepository;
    private final ImageGenerationService imageGenerationService;

    public PageDetailResponseDTO getPageDetail(PageRequestDTO requestDto) {
        Integer profileId = requestDto.getProfileId();
        Integer bookId = requestDto.getBookId();
        Integer pageNum = requestDto.getPageNum();

        // 해당 프로필 가져오기
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(bookId, profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 해당 책에 해당하는 페이지 가져오기
        PageEntity page = pageRepository.findByBookAndPageNumber(book, pageNum)
                .orElseThrow(() -> new PageNotFoundException(ErrorCode.PAGE_NOT_FOUND));

        // 페이지에 해당하는 모르는 단어 가져오기
        List<UnknownWordEntity> unknownWordEntities = unknownWordRepository.getByPage(page)
                .orElseThrow(() -> new UnknownWordNotFoundException(ErrorCode.UNKNOWN_NOT_FOUND));

        List<UnknownWordDTO> unknownWordDTOS = UnknownWordDTO.toDto(unknownWordEntities);

        return PageDetailResponseDTO.builder()
                .pageId(page.getId())
                .pageNumber(page.getPageNumber())
                .image(page.getImage())
                .content(page.getContent())
                .unknownWords(unknownWordDTOS)
                .build();
    }

    public PageDetailResponseDTO updatePageImage(PageRequestDTO requestDto, MultipartFile imageFile) {
        // 해당 프로필 가져오기
        ProfileEntity profile = profileRepository.findById(requestDto.getProfileId())
                .orElseThrow(() -> new ProfileNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 해당 프로필에 해당하는 책 가져오기
        BookEntity book = bookRepository.findByIdAndProfile(requestDto.getBookId(), profile)
                .orElseThrow(() -> new BookNotFoundException(ErrorCode.BOOK_NOT_FOUND));

        // 해당 책에 해당하는 페이지 가져오기
        PageEntity page = pageRepository.findByBookAndPageNumber(book, requestDto.getPageNum())
                .orElseThrow(() -> new PageNotFoundException(ErrorCode.PAGE_NOT_FOUND));

        // 페이지 이미지 생성 및 업로드
        String imageUrl = imageGenerationService.generateAndUploadPageImage(page.getContent());

        // 페이지 엔티티 업데이트
        page.setImage(imageUrl);
        pageRepository.save(page);

        // 처음 페이지가 로딩될 때 이미지가 삽입되기에, 처음 본다고 가정. unknownWords는 null로 반환
        return new PageDetailResponseDTO(page.getId(), page.getPageNumber(), imageUrl, page.getContent(), null);
    }
}
